package com.jesushz.analytics.domain

interface AnalyticsRepository {

    suspend fun getAnalyticsValues(): AnalyticsValues

}