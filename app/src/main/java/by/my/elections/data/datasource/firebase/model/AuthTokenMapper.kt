package by.my.elections.data.datasource.firebase.model

import com.google.firebase.auth.GetTokenResult
import by.my.elections.core.BaseMapper

class AuthTokenMapper : BaseMapper<GetTokenResult, AuthToken> {
    override fun map(from: GetTokenResult): AuthToken {
        return AuthToken(
            from.token!!,
            from.expirationTimestamp
        )
    }
}