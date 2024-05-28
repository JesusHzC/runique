package com.jesushzc.run.presentation.di

import com.jesushzc.run.presentation.active_run.ActiveRunViewModel
import com.jesushzc.run.presentation.run_overview.RunOverviewViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val runOverviewViewModelModule = module {
    viewModelOf(::RunOverviewViewModel)
    viewModelOf(::ActiveRunViewModel)
}

