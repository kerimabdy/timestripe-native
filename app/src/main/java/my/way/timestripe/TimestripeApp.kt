package my.way.timestripe

import android.app.Application
import my.way.timestripe.di.appModule
import my.way.timestripe.di.databaseModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class TimestripeApp: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@TimestripeApp)
            androidLogger()

            modules(
                appModule,
                databaseModule
            )
        }
    }
}