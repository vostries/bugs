package com.vostrik.buggame.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RecordDaoTest {
    private lateinit var database: AppDatabase
    private lateinit var recordDao: RecordDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        recordDao = database.recordDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun saveBestRecordKeepsOneMaximumPerProfileAndDifficulty() {
        val profileId = database.profileDao().insertProfile(UserProfile(fullName = "Test User"))
        recordDao.saveBestRecord(GameRecord(profileId = profileId, score =-10, difficulty = 2))
        val first = recordDao.getBestRecordForProfile(profileId, 2)!!

        recordDao.saveBestRecord(GameRecord(profileId = profileId, score =-20, difficulty = 2))
        assertEquals(first, recordDao.getBestRecordForProfile(profileId, difficulty = 2))

        recordDao.saveBestRecord(GameRecord(profileId = profileId, score =15, difficulty = 2))
        val best = recordDao.getBestRecordForProfile(profileId, 2)!!
        assertEquals(first.id, best.id)
        assertEquals(15, best.score)

        recordDao.saveBestRecord(GameRecord(profileId = profileId, score =100, difficulty = 3))
        assertEquals(2, recordDao.getTopRecords(10).size)
    }
}
