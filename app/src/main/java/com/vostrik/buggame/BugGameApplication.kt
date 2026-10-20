package com.vostrik.buggame

import android.app.Application
import com.vostrik.buggame.database.AppDatabase

class BugGameApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppDatabase.getDatabase(this)
    }
}
