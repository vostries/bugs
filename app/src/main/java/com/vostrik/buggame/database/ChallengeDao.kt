package com.vostrik.buggame.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface ChallengeDao {
    @Query("SELECT * FROM daily_challenges WHERE profileId = :profileId AND date = :date")
    fun getForDate(profileId: Long, date: String): List<DailyChallengeProgress>

    @Upsert
    fun upsert(row: DailyChallengeProgress)

    @Query("DELETE FROM daily_challenges WHERE profileId = :profileId AND date = :date")
    fun deleteForDate(profileId: Long, date: String)

    @Query("""
        SELECT SUM(xpReward) FROM daily_challenges
        WHERE profileId = :profileId AND completedAt IS NOT NULL
    """)
    fun getLifetimeXp(profileId: Long): Int?

    @Query("""
        SELECT date,
               SUM(CASE WHEN completedAt IS NOT NULL THEN xpReward ELSE 0 END) AS totalXp,
               SUM(CASE WHEN completedAt IS NOT NULL THEN 1 ELSE 0 END) AS completed,
               COUNT(*) AS total
        FROM daily_challenges
        WHERE profileId = :profileId
        GROUP BY date
        ORDER BY date DESC
    """)
    fun getHistory(profileId: Long): List<DayHistory>
}
