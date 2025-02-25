package com.jesushzc.run.data.di

import com.jesushzc.run.data.CreateRunWorker
import com.jesushzc.run.data.DeleteRunWorker
import com.jesushzc.run.data.FetchRunsWorker
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.dsl.module

val runDataModule = module {
    workerOf(::CreateRunWorker)
    workerOf(::DeleteRunWorker)
    workerOf(::FetchRunsWorker)
}
