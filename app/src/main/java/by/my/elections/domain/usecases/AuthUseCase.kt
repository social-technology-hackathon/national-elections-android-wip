package by.my.elections.domain.usecases

import by.my.elections.data.datasource.firebase.model.AuthStatus
import by.my.elections.domain.repository.UserRepository
import io.reactivex.Observable

class AuthUseCase(private val userRepository: UserRepository) :
    BaseObservableUseCase<AuthStatus> {
    override fun execute(): Observable<AuthStatus> {
        return userRepository.authStatus()
    }
}