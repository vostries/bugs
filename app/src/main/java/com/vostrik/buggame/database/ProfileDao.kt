package com.vostrik.buggame.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ProfileDao {
    @Query("SELECT * FROM user_profiles ORDER BY fullName")
    fun getAllProfiles(): List<UserProfile>

    @Query("SELECT * FROM user_profiles WHERE id = :id")
    fun getProfileById(id: Long): UserProfile?

    @Query("SELECT * FROM user_profiles WHERE fullName = :fullName")
    fun getProfileByName(fullName: String): UserProfile?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertProfile(profile: UserProfile): Long
}
