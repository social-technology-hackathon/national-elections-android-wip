package by.my.elections.presentation.di

import by.my.elections.core.ModuleProvider
import by.my.elections.presentation.SinglePresenter
import by.my.elections.presentation.login.LoginPresenter
import by.my.elections.presentation.main.MainPresenter
import by.my.elections.presentation.navigation.Navigator
import by.my.elections.presentation.splash.SplashPresenter
import org.koin.core.module.Module
import org.koin.dsl.module

object PresentationModule : ModuleProvider {

    private val presentationModule = module {
        single { Navigator() }

        factory { SinglePresenter(navigator = get(), schedulerProvider = get()) }

        factory {
            SplashPresenter(
                authUseCase = get(),
                updateUserUseCase = get(),
                navigator = get(),
                schedulerProvider = get()
            )
        }

        factory {
            LoginPresenter(
                signInWithCredentialUseCase = get(),
                buildAuthIntentUseCase = get(),
                updateUserUseCase = get(),
                navigator = get(),
                schedulerProvider = get()
            )
        }

        factory {
            MainPresenter(
                getLastKnownLocationUseCase = get(),
                uploadFileUseCase = get(),
                schedulerProvider = get()
            )
        }
    }

    override fun modules(): List<Module> {
        return listOf(presentationModule)
    }

}