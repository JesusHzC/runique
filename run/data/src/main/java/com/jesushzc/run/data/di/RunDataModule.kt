package com.jesushzc.run.data.di

import com.jesushzc.core.domain.run.SyncRunScheduler
import com.jesushzc.run.data.CreateRunWorker
import com.jesushzc.run.data.DeleteRunWorker
import com.jesushzc.run.data.FetchRunsWorker
import com.jesushzc.run.data.SyncRunWorkerScheduler
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val runDataModule = module {
    workerOf(::CreateRunWorker)
    workerOf(::DeleteRunWorker)
    workerOf(::FetchRunsWorker)

    singleOf(::SyncRunWorkerScheduler).bind<SyncRunScheduler>()
}
