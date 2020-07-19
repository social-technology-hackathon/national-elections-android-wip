package by.my.elections.domain.model

sealed class TrackModeStatus {
    object On : TrackModeStatus()
    object Off : TrackModeStatus()
    object PermissionNotGranted : TrackModeStatus()
    object NoLocation : TrackModeStatus()
}