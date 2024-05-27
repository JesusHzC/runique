package com.jesushzc.auth.data.di

import com.jesushzc.auth.data.EmailPatternValidator
import com.jesushzc.auth.domain.PatternValidator
import com.jesushzc.auth.domain.UserDataValidator
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val authDataModule = module {
    single<PatternValidator> {
        EmailPatternValidator
    }
    singleOf(::UserDataValidator)
}

