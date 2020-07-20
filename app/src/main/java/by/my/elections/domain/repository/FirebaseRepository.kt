package by.my.elections.domain.repository

import io.reactivex.Completable
import org.opencv.core.Mat

interface FirebaseRepository {
    fun uploadStream(stream: Mat): Completable
}