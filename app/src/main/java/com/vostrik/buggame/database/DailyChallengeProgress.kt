package com.vostrik.buggame.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "daily_challenges",
    foreignKeys = [
        ForeignKey(
            entity = UserProfile::class,
            parentColumns = ["id"],
            childColumns = ["profileId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["profileId", "date", "type"], unique = true),
        Index("profileId")
    ]
)
data class DailyChallengeProgress(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val profileId: Long,
    val date: String,           // "yyyy-MM-dd"
    val type: String,           // ChallengeType.name
    val goal: Int,
    val xpReward: Int,
    val progress: Int = 0,
    val completedAt: Long? = null
)

data class DayHistory(
    val date: String,
    val totalXp: Int,
    val completed: Int,
    val total: Int
)
