package by.my.elections.data.datasource.firebase.model

import com.google.firebase.firestore.PropertyName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import by.my.elections.data.annotation.DefaultIfNull

@DefaultIfNull
@JsonClass(generateAdapter = true)
data class AuthToken(
    @Json(name = "token")
    @PropertyName("token")
    val token: String,
    @Json(name = "expirationTimestamp")
    @PropertyName("expirationTimestamp")
    val expirationTimestamp: Long
) {
    fun isExpiredOrExpiring(): Boolean {
        val nowWithThreshold = (System.currentTimeMillis() / 1000 - 600)
        return expirationTimestamp <= nowWithThreshold
    }
}