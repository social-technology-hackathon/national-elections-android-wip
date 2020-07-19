package by.my.elections.data.datasource.firebase.model

sealed class AuthStatus {
    data class Authorized(val user: User) : AuthStatus()
    object NotAuthorized : AuthStatus()
}