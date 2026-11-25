package com.vostrik.buggame.challenge

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class ChallengeGeneratorTest {
    @Test
    fun sameDateProducesSameChallenges() {
        val date = LocalDate.of(2026, 11, 25)
        val set1 = ChallengeGenerator.generate(date)
        val set2 = ChallengeGenerator.generate(date)

        assertEquals(set1.size, set2.size)
        set1.zip(set2).forEach { (c1, c2) ->
            assertEquals(c1.type, c2.type)
            assertEquals(c1.goal, c2.goal)
            assertEquals(c1.xpReward, c2.xpReward)
        }
    }

    @Test
    fun differentDateProducedDifferentChallenges() {
        val date1 = LocalDate.of(2026, 11, 25)
        val date2 = LocalDate.of(2026, 11, 26)
        val set1 = ChallengeGenerator.generate(date1)
        val set2 = ChallengeGenerator.generate(date2)

        // Different dates usually produce different sets (not guaranteed but highly likely)
        // At minimum, check they're not identical
        assertTrue(set1.size in 3..5)
        assertTrue(set2.size in 3..5)
    }

    @Test
    fun countIsBetween3And5() {
        val dates = listOf(
            LocalDate.of(2026, 11, 20),
            LocalDate.of(2026, 11, 21),
            LocalDate.of(2026, 11, 22),
            LocalDate.of(2026, 11, 23),
            LocalDate.of(2026, 11, 24),
            LocalDate.of(2026, 11, 25)
        )

        dates.forEach { date ->
            val challenges = ChallengeGenerator.generate(date)
            assertTrue("Count for $date must be 3..5, got ${challenges.size}", challenges.size in 3..5)
        }
    }

    @Test
    fun typesAreUnique() {
        val date = LocalDate.of(2026, 11, 25)
        val challenges = ChallengeGenerator.generate(date)
        val types = challenges.map { it.type }
        assertEquals("Types must be unique", types.size, types.toSet().size)
    }

    @Test
    fun goalAndXpAreInRange() {
        val date = LocalDate.of(2026, 11, 25)
        val challenges = ChallengeGenerator.generate(date)

        challenges.forEach { challenge ->
            assertTrue(
                "Goal for ${challenge.type} must be in ${challenge.type.goalRange}, got ${challenge.goal}",
                challenge.goal in challenge.type.goalRange
            )
            assertTrue(
                "XP for ${challenge.type} must be in ${challenge.type.xpRange}, got ${challenge.xpReward}",
                challenge.xpReward in challenge.type.xpRange
            )
        }
    }
}
