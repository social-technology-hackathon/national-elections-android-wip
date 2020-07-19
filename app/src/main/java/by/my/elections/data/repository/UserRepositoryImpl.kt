package by.my.elections.data.repository

import by.my.elections.data.datasource.firebase.AuthDataSource
import by.my.elections.data.datasource.firebase.FirestoreDataSource
import by.my.elections.data.datasource.firebase.model.AuthStatus
import by.my.elections.data.datasource.firebase.model.User
import by.my.elections.domain.repository.UserRepository
import io.reactivex.Completable
import io.reactivex.Observable

class UserRepositoryImpl(
    private val firestoreDataSource: FirestoreDataSource,
    private val authDataSource: AuthDataSource
) : UserRepository {

    override fun authStatus(): Observable<AuthStatus> {
        return authDataSource.authStatus()
    }

    override fun updateUser(user: User): Completable {
        return firestoreDataSource.createOrUpdateUser(user)
    }
}