package com.vostrik.buggame.di

import com.vostrik.buggame.GameViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::GameViewModel)
}
