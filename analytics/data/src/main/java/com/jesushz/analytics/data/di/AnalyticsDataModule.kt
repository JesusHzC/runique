package com.jesushz.analytics.data.di

import com.jesushz.analytics.data.RoomAnalyticsRepository
import com.jesushz.analytics.domain.AnalyticsRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val analyticsModule = module {
    singleOf(::RoomAnalyticsRepository).bind<AnalyticsRepository>()
}