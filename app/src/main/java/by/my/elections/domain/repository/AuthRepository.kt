package by.my.elections.domain.repository

import by.my.elections.data.datasource.firebase.model.*
import io.reactivex.Single

interface AuthRepository {
    fun user() : Single<User>
    fun authToken(): Single<AuthToken>
    fun signInWithCredential(authIntent: AuthIntent): Single<AuthStatus>
    fun buildAuthIntent(): AuthIntent
}