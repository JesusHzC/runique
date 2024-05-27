package com.jesushzc.runique

import android.app.Application
import com.jesushzc.auth.data.di.authDataModule
import com.jesushzc.auth.presentation.di.authViewModelModule
import com.jesushzc.runique.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class RuniqueApp: Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidLogger()
            androidContext(this@RuniqueApp)
            modules(
                appModule,
                authDataModule,
                authViewModelModule
            )
        }
    }

}

