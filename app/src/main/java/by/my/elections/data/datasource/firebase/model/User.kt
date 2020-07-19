package by.my.elections.data.datasource.firebase.model

import com.google.firebase.firestore.PropertyName
import java.util.*

data class User(
    @PropertyName("uuid")
    val uuid: String = "",
    @PropertyName("name")
    val name: String = "",
    @PropertyName("email")
    val email: String = "",
    @PropertyName("photoUrl")
    val photoUrl: String = "",
    @PropertyName("lastSignIn")
    var lastSignIn: Date = Date()
)