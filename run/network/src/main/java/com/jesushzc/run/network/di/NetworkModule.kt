package com.jesushzc.run.network.di

import com.jesushzc.core.domain.run.RemoteRunDataSource
import com.jesushzc.run.network.KtorRemoteRunDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val networkModule = module {
    singleOf(::KtorRemoteRunDataSource).bind<RemoteRunDataSource>()
}