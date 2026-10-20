package com.vostrik.buggame

import android.app.Application
import com.vostrik.buggame.database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class BugGameApplication : Application() {
    val persistenceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        AppDatabase.getDatabase(this)
    }
}
