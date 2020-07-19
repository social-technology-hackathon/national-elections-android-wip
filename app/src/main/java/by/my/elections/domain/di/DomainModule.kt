package by.my.elections.domain.di

import by.my.elections.core.ModuleProvider
import by.my.elections.domain.usecases.*
import org.koin.core.module.Module
import org.koin.dsl.module

object DomainModule : ModuleProvider {
    private val useCaseModule = module {
        single {
            AuthUseCase(userRepository = get())
        }

        single {
            UpdateUserUseCase(userRepository = get(), authRepository = get())
        }

        single {
            BuildAuthIntentUseCase(
                authRepository = get()
            )
        }
        single {
            SignInWithCredentialUseCase(authRepository = get())
        }

        single {
            GetLastKnownLocationUseCase(locationRepository = get())
        }

        single {
            UploadFileUseCase(firebaseRepository = get())
        }


    }

    override fun modules(): List<Module> {
        return listOf(useCaseModule)
    }
}