package by.my.elections.domain.usecases

import by.my.elections.data.datasource.firebase.model.AuthIntent
import by.my.elections.data.datasource.firebase.model.AuthStatus
import by.my.elections.domain.repository.AuthRepository
import io.reactivex.Single

class SignInWithCredentialUseCase(private val authRepository: AuthRepository) :
    BaseSingleUseCaseWithParam<AuthIntent, AuthStatus> {

    override fun execute(param: AuthIntent): Single<AuthStatus> {
        return authRepository.signInWithCredential(param)
    }
}