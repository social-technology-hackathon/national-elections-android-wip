package by.my.elections.data.model

import com.google.firebase.firestore.PropertyName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import by.my.elections.data.annotation.DefaultIfNull

@DefaultIfNull
@JsonClass(generateAdapter = true)
data class UserRole(
    @Json(name = "type")
    @PropertyName("type")
    val type: String = ""
)