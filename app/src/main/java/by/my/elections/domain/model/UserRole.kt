package by.my.elections.domain.model

sealed class UserRole {
    object Guest : UserRole()
    object Member : UserRole()
    object Invalid : UserRole()
}