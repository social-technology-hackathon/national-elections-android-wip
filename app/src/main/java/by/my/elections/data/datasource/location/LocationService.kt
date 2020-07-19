package by.my.elections.data.datasource.location

import android.Manifest.permission
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import by.my.elections.data.model.DeviceLocation
import com.google.android.gms.location.*
import com.jakewharton.rx.replayingShare
import io.reactivex.Observable
import io.reactivex.ObservableEmitter

class LocationService(private val context: Context) {
    private val request = LocationRequest.create()
        .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
        .setSmallestDisplacement(10.0f)

    @SuppressLint("MissingPermission")
    private val locationObservable: Observable<DeviceLocation> =
        Observable.create { emitter: ObservableEmitter<DeviceLocation> ->
            val locationCallback: LocationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?) {
                    if (locationResult != null) {
                        val location: Location? = locationResult.locations.firstOrNull()
                        if (location != null) {
                            emitter.onNext(
                                DeviceLocation(
                                    latitude = location.latitude,
                                    longitude = location.longitude
                                )
                            )
                        }
                    }
                }
            }


            LocationServices.getFusedLocationProviderClient(context)
                .requestLocationUpdates(request, locationCallback, Looper.getMainLooper())
            emitter.setCancellable {
                LocationServices.getFusedLocationProviderClient(context)
                    .removeLocationUpdates(locationCallback)
            }
        }
            .replayingShare()


    @RequiresPermission(permission.ACCESS_FINE_LOCATION)
    fun deviceLocation() = locationObservable

}
