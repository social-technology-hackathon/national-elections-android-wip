package by.my.elections.data.datasource.firebase.model

import android.content.Intent

interface AuthIntent {
    fun getIntent(): Intent
}

fun Intent.authIntent(): AuthIntent {
    val intent = this
    return object : AuthIntent {
        override fun getIntent(): Intent {
            return intent
        }
    }
}