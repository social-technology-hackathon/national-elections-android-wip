package by.my.elections

import android.app.Application
import by.my.elections.data.di.DataModule
import by.my.elections.domain.di.DomainModule
import by.my.elections.presentation.di.PresentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber
import timber.log.Timber.DebugTree


class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(DebugTree())

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(
                DataModule.modules() + DomainModule.modules() + PresentationModule.modules()
            )
        }
    }
}