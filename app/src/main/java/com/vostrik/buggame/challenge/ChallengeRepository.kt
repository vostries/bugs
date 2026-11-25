package com.vostrik.buggame.challenge

import com.vostrik.buggame.BugType
import com.vostrik.buggame.GameResult
import com.vostrik.buggame.database.ChallengeDao
import com.vostrik.buggame.database.DailyChallengeProgress
import com.vostrik.buggame.database.DayHistory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.time.LocalDate

data class ChallengeSnapshot(
    val active: List<DailyChallenge>,
    val todayXpEarned: Int,
    val todayXpMax: Int,
    val lifetimeXp: Int,
    val history: List<DayHistory>
)

/**
 * Bridges deterministic daily challenge generation with persistent progress.
 *
 * **Limitations:**
 * - Timezone: "today" is the device's local date. A day flips at local midnight and updates only on
 *   the next access (game start / tab open); there is no background midnight timer. If the system
 *   time or timezone changes mid-session the set may change on the next access.
 * - Lazy generation: today's challenges are created on first access and stored once; days without
 *   activity never appear in history.
 * - Progress granularity: each game event is persisted immediately (read-modify-write guarded by a
 *   [Mutex]); round-based challenges (HIGH_SCORE, ACCURACY) are only applied at round end.
 * - Reward snapshot: `goal`/`xpReward` are stored per row, so future generator changes never rewrite
 *   past history.
 * - Profile-scoped: challenges require a selected profile, same as the game itself.
 * - Isolated XP: challenge XP is a separate meta-progression and never affects game records.
 */
class ChallengeRepository(
    private val dao: ChallengeDao,
    private val dateProvider: () -> LocalDate = { LocalDate.now() }
) {
    private val mutex = Mutex()

    suspend fun getSnapshot(profileId: Long): ChallengeSnapshot = withContext(Dispatchers.IO) {
        mutex.withLock {
            val active = ensureToday(profileId).map { it.toDomain() }
            ChallengeSnapshot(
                active = active,
                todayXpEarned = active.filter { it.isCompleted }.sumOf { it.xpReward },
                todayXpMax = active.sumOf { it.xpReward },
                lifetimeXp = dao.getLifetimeXp(profileId) ?: 0,
                history = dao.getHistory(profileId)
            )
        }
    }

    suspend fun applyBugKill(profileId: Long, bugType: BugType): List<DailyChallenge> =
        mutate(profileId) { ChallengeRules.applyBugKill(it, bugType) }

    suspend fun applyBonus(profileId: Long): List<DailyChallenge> =
        mutate(profileId) { ChallengeRules.applyBonus(it) }

    suspend fun applyRoundResult(profileId: Long, result: GameResult): List<DailyChallenge> =
        mutate(profileId) { ChallengeRules.applyRoundResult(it, result) }

    suspend fun lifetimeXp(profileId: Long): Int = withContext(Dispatchers.IO) {
        dao.getLifetimeXp(profileId) ?: 0
    }

    private suspend fun mutate(
        profileId: Long,
        rule: (List<DailyChallenge>) -> List<DailyChallenge>
    ): List<DailyChallenge> = withContext(Dispatchers.IO) {
        mutex.withLock {
            val entities = ensureToday(profileId)
            val updated = rule(entities.map { it.toDomain() })
            persistChanges(entities, updated)
            updated
        }
    }

    private fun ensureToday(profileId: Long): List<DailyChallengeProgress> {
        val today = dateProvider()
        val date = today.toString()
        val existing = dao.getForDate(profileId, date)
        if (existing.isNotEmpty()) return existing

        ChallengeGenerator.generate(today).forEach { domain ->
            dao.upsert(
                DailyChallengeProgress(
                    profileId = profileId,
                    date = date,
                    type = domain.type.name,
                    goal = domain.goal,
                    xpReward = domain.xpReward,
                    progress = domain.progress,
                    completedAt = domain.completedAt
                )
            )
        }
        return dao.getForDate(profileId, date)
    }

    private fun persistChanges(
        before: List<DailyChallengeProgress>,
        after: List<DailyChallenge>
    ) {
        before.forEachIndexed { index, entity ->
            val updated = after[index]
            if (entity.progress != updated.progress || entity.completedAt != updated.completedAt) {
                dao.upsert(entity.copy(progress = updated.progress, completedAt = updated.completedAt))
            }
        }
    }

    private fun DailyChallengeProgress.toDomain() = DailyChallenge(
        type = ChallengeType.valueOf(type),
        goal = goal,
        xpReward = xpReward,
        progress = progress,
        completedAt = completedAt
    )
}
