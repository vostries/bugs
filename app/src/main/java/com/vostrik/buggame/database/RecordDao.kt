package com.vostrik.buggame.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

data class RecordWithProfile(
    val id: Long,
    val profileId: Long,
    val fullName: String,
    val score: Int,
    val difficulty: Int,
    val timestamp: Long
)

@Dao
interface RecordDao {
    @Query("""
        SELECT r.id, r.profileId, p.fullName, r.score, r.difficulty, r.timestamp
        FROM game_records r
        INNER JOIN user_profiles p ON r.profileId = p.id
        ORDER BY r.score DESC
        LIMIT :limit
    """)
    fun getTopRecords(limit: Int = 10): List<RecordWithProfile>

    @Query("""
        SELECT r.id, r.profileId, p.fullName, r.score, r.difficulty, r.timestamp
        FROM game_records r
        INNER JOIN user_profiles p ON r.profileId = p.id
        WHERE r.difficulty = :difficulty
        ORDER BY r.score DESC
        LIMIT :limit
    """)
    fun getTopRecordsByDifficulty(difficulty: Int, limit: Int = 10): List<RecordWithProfile>

    @Query("""
        SELECT * FROM game_records
        WHERE profileId = :profileId AND difficulty = :difficulty
        ORDER BY score DESC
        LIMIT 1
    """)
    fun getBestRecordForProfile(profileId: Long, difficulty: Int): GameRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecord(record: GameRecord): Long

    @Query("UPDATE game_records SET score = :newScore, timestamp = :timestamp WHERE id = :recordId")
    fun updateRecord(recordId: Long, newScore: Int, timestamp: Long)

    @Transaction
    fun saveBestRecord(record: GameRecord) {
        require(record.difficulty in 1..5)
        val best = getBestRecordForProfile(record.profileId, record.difficulty)
        if (best == null) {
            insertRecord(record)
        } else if (record.score > best.score) {
            updateRecord(best.id, record.score, record.timestamp)
        }
    }
}
