package com.jesushzc.core.data.di

import com.jesushzc.core.data.auth.EncryptedSessionStorage
import com.jesushzc.core.data.networking.HttpClientFactory
import com.jesushzc.core.domain.SessionStorage
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreDataModule = module {
    single {
        HttpClientFactory(get()).build()
    }
    singleOf(::EncryptedSessionStorage).bind<SessionStorage>()
}

