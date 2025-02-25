package com.jesushzc.core.database.di

import androidx.room.Room
import com.jesushzc.core.database.RoomLocalRunDataSource
import com.jesushzc.core.database.RunDatabase
import com.jesushzc.core.domain.run.LocalRunDataSource
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidApplication(),
            RunDatabase::class.java,
            "run.db"
        ).fallbackToDestructiveMigration().build()
    }

    single { get<RunDatabase>().runDao }

    singleOf(::RoomLocalRunDataSource).bind<LocalRunDataSource>()
}
