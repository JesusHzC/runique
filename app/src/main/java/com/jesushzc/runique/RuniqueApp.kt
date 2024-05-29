package com.jesushzc.runique

import android.app.Application
import com.jesushzc.auth.data.di.authDataModule
import com.jesushzc.auth.presentation.di.authViewModelModule
import com.jesushzc.core.data.di.coreDataModule
import com.jesushzc.run.location.di.locationModule
import com.jesushzc.run.presentation.di.runPresentationModule
import com.jesushzc.runique.di.appModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class RuniqueApp: Application() {

    val applicationScope = CoroutineScope(SupervisorJob())

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
                authViewModelModule,
                coreDataModule,
                runPresentationModule,
                locationModule
            )
        }
    }

}

