package com.jesushzc.run.presentation.active_run

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jesushzc.core.domain.location.Location
import com.jesushzc.core.domain.run.Run
import com.jesushzc.core.domain.run.RunRepository
import com.jesushzc.core.domain.util.Result
import com.jesushzc.core.presentation.ui.asUiText
import com.jesushzc.run.domain.LocationDataCalculator
import com.jesushzc.run.domain.RunningTracker
import com.jesushzc.run.presentation.active_run.service.ActiveRunService
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.time.ZonedDateTime

class ActiveRunViewModel(
    private val runningTracker: RunningTracker,
    private val runRepository: RunRepository
): ViewModel() {

    var state by mutableStateOf(
        ActiveRunState(
            shouldTrack = ActiveRunService.isServiceActive and runningTracker.isTracking.value,
            hasStartedRunning = ActiveRunService.isServiceActive,
        )
    )
        private set

    private val _eventChannel = Channel<ActiveRunEvent>()
    val eventChannel = _eventChannel.receiveAsFlow()

    private val _hasLocationPermission = MutableStateFlow(false)
    val hasLocationPermission = _hasLocationPermission.asStateFlow()

    private val shouldTrack = snapshotFlow { state.shouldTrack }
        .stateIn(viewModelScope, SharingStarted.Lazily, state.shouldTrack)

    private val isTracking = combine(
        shouldTrack,
        _hasLocationPermission
    ) { shouldTrack, hasPermission ->
        shouldTrack and hasPermission
    }.stateIn(viewModelScope, SharingStarted.Lazily, false)

    init {
        _hasLocationPermission
            .onEach { hasPermission ->
                if (hasPermission) {
                    runningTracker.startObservingLocation()
                } else {
                    runningTracker.stopObservingLocation()
                }
            }
            .launchIn(viewModelScope)

        isTracking
            .onEach { isTracking ->
                runningTracker.setIsTracking(isTracking)
            }
            .launchIn(viewModelScope)

        runningTracker
            .currentLocation
            .onEach { locationWithAltitude ->
                state = state.copy(
                    currentLocation = locationWithAltitude?.location
                )
            }
            .launchIn(viewModelScope)

        runningTracker
            .runData
            .onEach { runData ->
                state = state.copy(
                    runData = runData
                )
            }
            .launchIn(viewModelScope)

        runningTracker
            .elapsedTime
            .onEach {
                state = state.copy(
                    elapsedTime = it
                )
            }
            .launchIn(viewModelScope)
    }

    fun onAction(action: ActiveRunAction) {
        when (action) {
            ActiveRunAction.OnFinishRunClick -> {
                state = state.copy(
                    isRunFinished = true,
                    isSavingRun = true
                )
            }
            ActiveRunAction.OnResumeRunClick -> {
                state = state.copy(
                    shouldTrack = true
                )
            }
            ActiveRunAction.OnToggleRunClick -> {
                state = state.copy(
                    hasStartedRunning = true,
                    shouldTrack = !state.shouldTrack
                )
            }
            is ActiveRunAction.SubmitLocationPermissionInfo -> {
                _hasLocationPermission.value = action.acceptedLocationPermission
                state = state.copy(
                    showLocationPermissionRationale = action.showLocationRationale
                )
            }
            is ActiveRunAction.SubmitNotificationPermissionInfo -> {
                state = state.copy(
                    showNotificationPermissionRationale = action.showNotificationRationale
                )
            }
            ActiveRunAction.DismissRationaleDialog -> {
                state = state.copy(
                    showLocationPermissionRationale = false,
                    showNotificationPermissionRationale = false
                )
            }
            ActiveRunAction.OnBackClick -> {
                state = state.copy(
                    shouldTrack = false
                )
            }
            is ActiveRunAction.OnRunProcessed -> {
                finishRun(action.mapPictureBytes)
            }
        }
    }

    private fun finishRun(mapPictureBytes: ByteArray) {
        val locations = state.runData.locations
        if(locations.isEmpty() || locations.first().size <= 1) {
            state = state.copy(isSavingRun = false)
            return
        }

        viewModelScope.launch {
            val run = Run(
                id = null,
                duration = state.elapsedTime,
                dateTimeUtc = ZonedDateTime.now()
                    .withZoneSameInstant(ZoneId.of("UTC")),
                distanceMeters = state.runData.distanceMeters,
                location = state.currentLocation ?: Location(0.0, 0.0),
                maxSpeedKmh = LocationDataCalculator.getMaxSpeedKmh(locations),
                totalElevationMeters = LocationDataCalculator.getTotalElevationMeters(locations),
                mapPictureUrl = null
            )
            runningTracker.finishRun()

            when (val result = runRepository.upsertRun(run, mapPictureBytes)) {
                is Result.Error -> {
                    _eventChannel.send(ActiveRunEvent.Error(result.error.asUiText()))
                }
                is Result.Success -> {
                    _eventChannel.send(ActiveRunEvent.RunSaved)
                }
            }

            state = state.copy(isSavingRun = false)
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (!ActiveRunService.isServiceActive) {
            runningTracker.stopObservingLocation()
        }
    }

}

