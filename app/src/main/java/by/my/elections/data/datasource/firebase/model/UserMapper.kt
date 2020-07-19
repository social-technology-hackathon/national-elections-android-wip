package by.my.elections.data.datasource.firebase.model

import com.google.firebase.auth.FirebaseUser
import by.my.elections.core.BaseMapper

class UserMapper : BaseMapper<FirebaseUser, User> {
    override fun map(from: FirebaseUser): User {
        return User(
            from.uid,
            from.displayName ?: "",
            from.email ?: "",
            from.photoUrl?.toString() ?: ""
        )
    }
}