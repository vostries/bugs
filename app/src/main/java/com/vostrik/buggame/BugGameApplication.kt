package com.vostrik.buggame

import android.app.Application
import com.vostrik.buggame.database.AppDatabase
import com.vostrik.buggame.di.appModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class BugGameApplication : Application() {
    val persistenceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@BugGameApplication)
            modules(appModule)
        }
        AppDatabase.getDatabase(this)
    }
}
