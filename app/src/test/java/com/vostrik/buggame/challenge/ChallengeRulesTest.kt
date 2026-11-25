package com.vostrik.buggame.challenge

import com.vostrik.buggame.BugType
import com.vostrik.buggame.GameResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ChallengeRulesTest {
    @Test
    fun applyBugKillIncrementsCorrectType() {
        val challenges = listOf(
            DailyChallenge(ChallengeType.GOLDEN_BUGS, goal = 3, xpReward = 500),
            DailyChallenge(ChallengeType.FAST_BUGS, goal = 5, xpReward = 300),
            DailyChallenge(ChallengeType.HITS, goal = 10, xpReward = 200)
        )

        val updated = ChallengeRules.applyBugKill(challenges, BugType.GOLDEN)
        assertEquals(1, updated[0].progress)
        assertEquals(0, updated[1].progress)
        assertEquals(1, updated[2].progress)
    }

    @Test
    fun applyBugKillCompletesWhenGoalReached() {
        val challenge = DailyChallenge(
            ChallengeType.GOLDEN_BUGS,
            goal = 1,
            xpReward = 500,
            progress = 0
        )

        val updated = ChallengeRules.applyBugKill(listOf(challenge), BugType.GOLDEN)
        assertEquals(1, updated[0].progress)
        assertNotNull(updated[0].completedAt)
    }

    @Test
    fun applyBugKillDoesNotCompleteIncompleteChallenge() {
        val challenge = DailyChallenge(
            ChallengeType.GOLDEN_BUGS,
            goal = 3,
            xpReward = 500,
            progress = 1
        )

        val updated = ChallengeRules.applyBugKill(listOf(challenge), BugType.GOLDEN)
        assertEquals(2, updated[0].progress)
        assertNull(updated[0].completedAt)
    }

    @Test
    fun applyBugKillIgnoresAlreadyCompleted() {
        val challenge = DailyChallenge(
            ChallengeType.GOLDEN_BUGS,
            goal = 3,
            xpReward = 500,
            progress = 3,
            completedAt = 123L
        )

        val updated = ChallengeRules.applyBugKill(listOf(challenge), BugType.GOLDEN)
        assertEquals(3, updated[0].progress)
        assertEquals(123L, updated[0].completedAt)
    }

    @Test
    fun applyBonusIncrementsBonus() {
        val challenges = listOf(
            DailyChallenge(ChallengeType.BONUSES, goal = 2, xpReward = 400),
            DailyChallenge(ChallengeType.HITS, goal = 10, xpReward = 200)
        )

        val updated = ChallengeRules.applyBonus(challenges)
        assertEquals(1, updated[0].progress)
        assertEquals(0, updated[1].progress)
    }

    @Test
    fun applyRoundResultHighScore() {
        val challenge = DailyChallenge(
            ChallengeType.HIGH_SCORE,
            goal = 500,
            xpReward = 400,
            progress = 300
        )

        val result = GameResult(score = 600, hits = 10, misses = 2, accuracy = 83)
        val updated = ChallengeRules.applyRoundResult(listOf(challenge), result)

        assertEquals(600, updated[0].progress)
        assertNotNull(updated[0].completedAt)
    }

    @Test
    fun applyRoundResultHighScoreTakesMax() {
        val challenge = DailyChallenge(
            ChallengeType.HIGH_SCORE,
            goal = 500,
            xpReward = 400,
            progress = 600
        )

        val result = GameResult(score = 400, hits = 10, misses = 2, accuracy = 83)
        val updated = ChallengeRules.applyRoundResult(listOf(challenge), result)

        assertEquals(600, updated[0].progress)
    }

    @Test
    fun applyRoundResultAccuracy() {
        val challenge = DailyChallenge(
            ChallengeType.ACCURACY,
            goal = 80,
            xpReward = 300,
            progress = 70
        )

        val result = GameResult(score = 500, hits = 10, misses = 2, accuracy = 83)
        val updated = ChallengeRules.applyRoundResult(listOf(challenge), result)

        assertEquals(83, updated[0].progress)
        assertNotNull(updated[0].completedAt)
    }

    @Test
    fun multipleEventsApplyInSequence() {
        var challenges = listOf(
            DailyChallenge(ChallengeType.GOLDEN_BUGS, goal = 2, xpReward = 500),
            DailyChallenge(ChallengeType.HITS, goal = 3, xpReward = 200)
        )

        challenges = ChallengeRules.applyBugKill(challenges, BugType.GOLDEN)
        assertEquals(1, challenges[0].progress)
        assertEquals(1, challenges[1].progress)

        challenges = ChallengeRules.applyBugKill(challenges, BugType.GOLDEN)
        assertEquals(2, challenges[0].progress)
        assertEquals(2, challenges[1].progress)
        assertNotNull(challenges[0].completedAt)

        challenges = ChallengeRules.applyBugKill(challenges, BugType.GOLDEN)
        assertEquals(2, challenges[0].progress)
        assertEquals(3, challenges[1].progress)
    }
}
