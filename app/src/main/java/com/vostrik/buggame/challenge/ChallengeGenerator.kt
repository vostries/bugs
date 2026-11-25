package com.vostrik.buggame.challenge

import java.time.LocalDate
import kotlin.random.Random

/**
 * Generates daily challenges deterministically based on date.
 *
 * **Limitations:**
 * - Timezone: "today" is determined by device's local date. Day changes at midnight in local time.
 *   If system time/timezone is adjusted during a session, the challenge set may change mid-session.
 *   Updates only on next app start or tab reload.
 * - Generation is lazy: challenges are created on first access per day (game start or tab open),
 *   not at midnight. Days with no activity do not appear in history.
 * - Seed is based on `date.toEpochDay()`, so same date always produces identical set.
 */
object ChallengeGenerator {
    fun generate(date: LocalDate): List<DailyChallenge> {
        val rng = Random(date.toEpochDay())
        val count = 3 + rng.nextInt(3)
        val types = ChallengeType.entries.shuffled(rng).take(count)

        return types.map { type ->
            DailyChallenge(
                type = type,
                goal = type.goalRange.random(rng),
                xpReward = type.xpRange.random(rng)
            )
        }
    }
}
