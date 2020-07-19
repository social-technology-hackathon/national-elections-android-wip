package by.my.elections.domain.repository

import io.reactivex.Completable

interface FirebaseRepository {
    fun uploadStream(stream: ByteArray): Completable
}