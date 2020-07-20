package by.my.elections.data.repository

import android.Manifest
import androidx.annotation.RequiresPermission
import by.my.elections.data.datasource.firebase.AuthDataSource
import by.my.elections.data.datasource.firebase.FirestoreDataSource
import by.my.elections.data.datasource.location.LocationService
import by.my.elections.data.model.DeviceLocation
import by.my.elections.domain.repository.FirebaseRepository
import io.reactivex.Completable
import io.reactivex.Single
import org.opencv.core.Mat
import java.util.concurrent.TimeUnit

class FirebaseRepositoryImpl(
    private val firestoreDataSource: FirestoreDataSource,
    private val locationService: LocationService,
    private val authDataSource: AuthDataSource
) : FirebaseRepository {

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    override fun uploadStream(stream: Mat): Completable {
        return authDataSource.user()
            .flatMap { user ->
                locationService.deviceLocation()
                    .firstOrError()
                    .timeout(1, TimeUnit.SECONDS)
                    .onErrorResumeNext { Single.just(DeviceLocation(0.0, 0.0)) }
                    .map { deviceLocation ->
                        mapOf("user" to user.email, "location" to deviceLocation.toString())
                    }
            }
            .flatMapCompletable { firestoreDataSource.upload(stream, it) }

    }
}