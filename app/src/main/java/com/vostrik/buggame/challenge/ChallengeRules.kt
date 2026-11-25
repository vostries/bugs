package com.vostrik.buggame.challenge

import com.vostrik.buggame.BugType
import com.vostrik.buggame.GameResult

/**
 * Pure logic for applying game events to challenge progress.
 *
 * **Limitations:**
 * - Cumulative challenges: counter increments on each event, always monotonic.
 * - Round-based challenges (HIGH_SCORE, ACCURACY): progress takes max value, never decreases.
 * - Completion: `isCompleted` is set when `progress >= goal` and `completedAt` was null.
 * - NORMAL bug kills only increment HITS (no separate NORMAL_BUGS type).
 */
object ChallengeRules {
    fun applyBugKill(challenges: List<DailyChallenge>, bugType: BugType): List<DailyChallenge> {
        val now = System.currentTimeMillis()
        return challenges.map { challenge ->
            val newChallenge = when {
                challenge.isCompleted -> challenge
                challenge.type == ChallengeType.GOLDEN_BUGS && bugType == BugType.GOLDEN ->
                    challenge.copy(progress = challenge.progress + 1)
                challenge.type == ChallengeType.FAST_BUGS && bugType == BugType.FAST ->
                    challenge.copy(progress = challenge.progress + 1)
                challenge.type == ChallengeType.RARE_BUGS && bugType == BugType.RARE ->
                    challenge.copy(progress = challenge.progress + 1)
                challenge.type == ChallengeType.HITS ->
                    challenge.copy(progress = challenge.progress + 1)
                else -> challenge
            }
            if (newChallenge.progress >= newChallenge.goal && newChallenge.completedAt == null) {
                newChallenge.copy(completedAt = now)
            } else {
                newChallenge
            }
        }
    }

    fun applyBonus(challenges: List<DailyChallenge>): List<DailyChallenge> {
        val now = System.currentTimeMillis()
        return challenges.map { challenge ->
            val newChallenge = when {
                challenge.isCompleted -> challenge
                challenge.type == ChallengeType.BONUSES ->
                    challenge.copy(progress = challenge.progress + 1)
                else -> challenge
            }
            if (newChallenge.progress >= newChallenge.goal && newChallenge.completedAt == null) {
                newChallenge.copy(completedAt = now)
            } else {
                newChallenge
            }
        }
    }

    fun applyRoundResult(challenges: List<DailyChallenge>, result: GameResult): List<DailyChallenge> {
        val now = System.currentTimeMillis()
        return challenges.map { challenge ->
            val newChallenge = when {
                challenge.isCompleted -> challenge
                challenge.type == ChallengeType.HIGH_SCORE ->
                    challenge.copy(progress = maxOf(challenge.progress, result.score))
                challenge.type == ChallengeType.ACCURACY ->
                    challenge.copy(progress = maxOf(challenge.progress, result.accuracy))
                else -> challenge
            }
            if (newChallenge.progress >= newChallenge.goal && newChallenge.completedAt == null) {
                newChallenge.copy(completedAt = now)
            } else {
                newChallenge
            }
        }
    }
}
