package by.my.elections.domain.usecases

import by.my.elections.domain.repository.AuthRepository
import by.my.elections.domain.repository.UserRepository
import io.reactivex.Completable

class UpdateUserUseCase(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) :
    BaseCompletableUseCase {

    override fun execute(): Completable {
        return authRepository.user().flatMapCompletable { userRepository.updateUser(user = it) }
    }
}