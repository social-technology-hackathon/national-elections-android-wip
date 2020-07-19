package by.my.elections.domain.usecases

import by.my.elections.data.model.DeviceLocation
import by.my.elections.domain.repository.LocationRepository
import io.reactivex.Single

class GetLastKnownLocationUseCase(private val locationRepository: LocationRepository) :
    BaseSingleUseCase<DeviceLocation> {
    override fun execute(): Single<DeviceLocation> {
        return locationRepository.lastKnowLocation()
    }

}