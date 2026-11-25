package com.vostrik.buggame.di

import com.vostrik.buggame.GameViewModel
import com.vostrik.buggame.challenge.ChallengeRepository
import com.vostrik.buggame.challenge.ChallengesViewModel
import com.vostrik.buggame.database.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single { AppDatabase.getDatabase(androidContext()) }
    single { get<AppDatabase>().challengeDao() }
    single { ChallengeRepository(get()) }
    viewModelOf(::GameViewModel)
    viewModelOf(::ChallengesViewModel)
}
