package com.vostrik.buggame.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [UserProfile::class, GameRecord::class, DailyChallengeProgress::class],
    version = 3,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
    abstract fun recordDao(): RecordDao
    abstract fun challengeDao(): ChallengeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `daily_challenges` (
                        `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                        `profileId` INTEGER NOT NULL,
                        `date` TEXT NOT NULL,
                        `type` TEXT NOT NULL,
                        `goal` INTEGER NOT NULL,
                        `xpReward` INTEGER NOT NULL,
                        `progress` INTEGER NOT NULL DEFAULT 0,
                        `completedAt` INTEGER,
                        FOREIGN KEY(`profileId`) REFERENCES `user_profiles`(`id`) ON DELETE CASCADE
                    )
                """.trimIndent())
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_daily_challenges_profileId_date_type` ON `daily_challenges` (`profileId`, `date`, `type`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_daily_challenges_profileId` ON `daily_challenges` (`profileId`)")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bugs_database"
                ).addMigrations(MIGRATION_2_3).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
