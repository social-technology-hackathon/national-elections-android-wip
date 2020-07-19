package by.my.elections.domain.usecases

import by.my.elections.data.datasource.firebase.model.AuthIntent
import by.my.elections.domain.repository.AuthRepository
import io.reactivex.Single

class BuildAuthIntentUseCase(private val authRepository: AuthRepository) :
    BaseSingleUseCase<AuthIntent> {

    override fun execute(): Single<AuthIntent> {
        return Single.fromCallable {
            authRepository.buildAuthIntent()
        }
    }
}