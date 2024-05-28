package com.jesushzc.auth.data.di

import com.jesushzc.auth.data.EmailPatternValidator
import com.jesushzc.auth.data.repository.AuthRepositoryImpl
import com.jesushzc.auth.domain.PatternValidator
import com.jesushzc.auth.domain.UserDataValidator
import com.jesushzc.auth.domain.repository.AuthRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val authDataModule = module {
    single<PatternValidator> {
        EmailPatternValidator
    }
    singleOf(::UserDataValidator)

    singleOf(::AuthRepositoryImpl).bind<AuthRepository>()
}

