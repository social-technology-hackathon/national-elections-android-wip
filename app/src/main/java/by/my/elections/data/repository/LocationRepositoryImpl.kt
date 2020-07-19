package by.my.elections.data.repository

import android.Manifest
import androidx.annotation.RequiresPermission
import by.my.elections.data.datasource.location.LocationService
import by.my.elections.data.model.DeviceLocation
import by.my.elections.domain.repository.LocationRepository
import io.reactivex.Observable
import io.reactivex.Single


class LocationRepositoryImpl(private val locationService: LocationService) : LocationRepository {
    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    override fun location(): Observable<DeviceLocation> {
        return locationService.deviceLocation()
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    override fun lastKnowLocation(): Single<DeviceLocation> {
        return locationService.deviceLocation().firstOrError()
    }

}