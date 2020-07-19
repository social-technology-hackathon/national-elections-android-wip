package by.my.elections.data.repository

import by.my.elections.data.datasource.firebase.AuthDataSource
import by.my.elections.data.datasource.firebase.model.*
import by.my.elections.data.datasource.storage.SecureStorageDataSource
import by.my.elections.data.datasource.storage.exception.NotCachedValueException
import by.my.elections.domain.repository.AuthRepository
import io.reactivex.Single

class AuthRepositoryImpl(
    private val secureStorageDataSource: SecureStorageDataSource,
    private val authDataSource: AuthDataSource
) : AuthRepository {

    override fun user(): Single<User> {
        return authDataSource.user()
    }

    override fun authToken(): Single<AuthToken> {
        return cachedToken()
            .onErrorResumeNext(loadTokenAndSave())
    }

    override fun signInWithCredential(authIntent: AuthIntent) =
        authDataSource.signInWithCredential(data = authIntent.getIntent())

    override fun buildAuthIntent(): AuthIntent = authDataSource.authIntent()

    private fun loadTokenAndSave(): Single<AuthToken> {
        return authDataSource.authToken()
            .doOnSuccess {
                secureStorageDataSource.put(AUTH_TOKEN_KEY, it)
            }
    }

    private fun cachedToken(): Single<AuthToken> {
        return Single.fromCallable {
            val token = secureStorageDataSource[AUTH_TOKEN_KEY, AuthToken::class.java]
                ?: throw NotCachedValueException(AUTH_TOKEN_KEY)
            if (token.isExpiredOrExpiring()) {
                throw NotCachedValueException(AUTH_TOKEN_KEY)
            }
            token
        }

    }


    companion object {
        const val AUTH_TOKEN_KEY = ".auth_token"
    }
}