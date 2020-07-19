package by.my.elections.domain.repository

import by.my.elections.data.datasource.firebase.model.AuthStatus
import by.my.elections.data.datasource.firebase.model.User
import io.reactivex.Completable
import io.reactivex.Observable

interface UserRepository {
    fun authStatus(): Observable<AuthStatus>
    fun updateUser(user: User): Completable
}