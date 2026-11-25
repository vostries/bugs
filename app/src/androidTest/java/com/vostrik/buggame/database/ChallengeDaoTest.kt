package com.vostrik.buggame.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChallengeDaoTest {
    private lateinit var database: AppDatabase
    private lateinit var challengeDao: ChallengeDao
    private var profileId: Long = 0

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        challengeDao = database.challengeDao()
        profileId = database.profileDao().insertProfile(UserProfile(fullName = "Test User"))
    }

    @After
    fun tearDown() {
        database.close()
    }

    private fun row(date: String, type: String, xp: Int, progress: Int = 0, completedAt: Long? = null) =
        DailyChallengeProgress(
            profileId = profileId, date = date, type = type,
            goal = 10, xpReward = xp, progress = progress, completedAt = completedAt
        )

    @Test
    fun lifetimeXpCountsOnlyCompleted() {
        challengeDao.upsert(row("2026-11-25", "GOLDEN_BUGS", 500, progress = 10, completedAt = 1L))
        challengeDao.upsert(row("2026-11-25", "HITS", 300, progress = 3))
        challengeDao.upsert(row("2026-11-24", "BONUSES", 200, progress = 2, completedAt = 2L))

        assertEquals(700, challengeDao.getLifetimeXp(profileId))
    }

    @Test
    fun lifetimeXpIsNullWhenNothingCompleted() {
        challengeDao.upsert(row("2026-11-25", "HITS", 300, progress = 3))
        assertNull(challengeDao.getLifetimeXp(profileId))
    }

    @Test
    fun getForDateReturnsOnlyThatDay() {
        challengeDao.upsert(row("2026-11-25", "GOLDEN_BUGS", 500))
        challengeDao.upsert(row("2026-11-25", "HITS", 300))
        challengeDao.upsert(row("2026-11-24", "BONUSES", 200))

        val today = challengeDao.getForDate(profileId, "2026-11-25")
        assertEquals(2, today.size)
        assertEquals(setOf("GOLDEN_BUGS", "HITS"), today.map { it.type }.toSet())
    }

    @Test
    fun historyGroupsByDateAndCountsCompleted() {
        challengeDao.upsert(row("2026-11-25", "GOLDEN_BUGS", 500, progress = 10, completedAt = 1L))
        challengeDao.upsert(row("2026-11-25", "HITS", 300))
        challengeDao.upsert(row("2026-11-24", "BONUSES", 200, progress = 2, completedAt = 2L))

        val history = challengeDao.getHistory(profileId)
        assertEquals(listOf("2026-11-25", "2026-11-24"), history.map { it.date })

        val today = history.first { it.date == "2026-11-25" }
        assertEquals(500, today.totalXp)
        assertEquals(1, today.completed)
        assertEquals(2, today.total)

        val yesterday = history.first { it.date == "2026-11-24" }
        assertEquals(200, yesterday.totalXp)
        assertEquals(1, yesterday.completed)
        assertEquals(1, yesterday.total)
    }
}
