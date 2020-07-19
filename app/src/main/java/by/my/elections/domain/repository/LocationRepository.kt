package by.my.elections.domain.repository

import by.my.elections.data.model.DeviceLocation
import io.reactivex.Observable
import io.reactivex.Single

interface LocationRepository {
    fun location(): Observable<DeviceLocation>
    fun lastKnowLocation(): Single<DeviceLocation>
}