package com.jesushz.wear.app.presentation.di

import com.jesushz.wear.app.presentation.RuniqueApp
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val appModule = module {
    single {
        (androidApplication() as RuniqueApp).applicationScope
    }
}