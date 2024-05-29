package com.jesushzc.run.location.di

import com.jesushzc.run.domain.LocationObserver
import com.jesushzc.run.location.AndroidLocationObserver
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val locationModule = module {
    singleOf(::AndroidLocationObserver).bind<LocationObserver>()
}

