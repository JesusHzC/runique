package com.jesushzc.run.presentation.active_run

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jesushz.core.connectivity.domain.messaging.MessagingAction
import com.jesushzc.core.domain.location.Location
import com.jesushzc.core.domain.run.Run
import com.jesushzc.core.domain.run.RunRepository
import com.jesushzc.core.domain.util.Result
import com.jesushzc.core.presentation.ui.asUiText
import com.jesushzc.run.domain.LocationDataCalculator
import com.jesushzc.run.domain.RunningTracker
import com.jesushzc.run.domain.WatchConnector
import com.jesushz.core.notification.ActiveRunService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.math.roundToInt

class ActiveRunViewModel(
    private val runningTracker: RunningTracker,
    private val runRepository: RunRepository,
    private val watchConnector: WatchConnector,
    private val applicationScope: CoroutineScope
): ViewModel() {

    var state by mutableStateOf(
        ActiveRunState(
            shouldTrack = ActiveRunService.isServiceActive.value and runningTracker.isTracking.value,
            hasStartedRunning = ActiveRunService.isServiceActive.value,
        )
    )
        private set

    private val _eventChannel = Channel<ActiveRunEvent>()
    val eventChannel = _eventChannel.receiveAsFlow()

    private val _hasLocationPermission = MutableStateFlow(false)

    private val shouldTrack = snapshotFlow { state.shouldTrack }
        .stateIn(viewModelScope, SharingStarted.Lazily, state.shouldTrack)

    private val isTracking = combine(
        shouldTrack,
        _hasLocationPermission
    ) { shouldTrack, hasPermission ->
        shouldTrack and hasPermission
    }.stateIn(viewModelScope, SharingStarted.Lazily, false)

    init {
        watchConnector
            .connectedDevice
            .filterNotNull()
            .onEach { connectedDevice ->
                Timber.d("New device detected: ${connectedDevice.displayName}")
            }
            .launchIn(viewModelScope)

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

        listenToWatchActions()
    }

    fun onAction(action: ActiveRunAction, triggeredOnWatch: Boolean = false) {
        if (!triggeredOnWatch) {
            val messagingAction = when (action) {
                ActiveRunAction.OnFinishRunClick -> MessagingAction.Finish
                ActiveRunAction.OnResumeRunClick -> MessagingAction.StartOrResume
                ActiveRunAction.OnToggleRunClick -> {
                    if (state.hasStartedRunning) {
                        MessagingAction.Pause
                    } else {
                        MessagingAction.StartOrResume
                    }
                }
                else -> null
            }

            messagingAction?.let {
                viewModelScope.launch {
                    watchConnector.sendActionToWatch(it)
                }
            }
        }

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
                mapPictureUrl = null,
                avgHeartRate = if (state.runData.heartRates.isEmpty()) {
                    null
                } else {
                    state.runData.heartRates.average().roundToInt()
                },
                maxHeartRate = if (state.runData.heartRates.isEmpty()) {
                    null
                } else {
                    state.runData.heartRates.max()
                }
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

    private fun listenToWatchActions() {
        watchConnector
            .messagingActions
            .onEach { action ->
                when (action) {
                    MessagingAction.ConnectionRequest -> {
                        if (isTracking.value) {
                            watchConnector
                                .sendActionToWatch(MessagingAction.StartOrResume)
                        }
                    }
                    MessagingAction.Finish -> {
                        onAction(
                            action = ActiveRunAction.OnFinishRunClick,
                            triggeredOnWatch = true
                        )
                    }
                    MessagingAction.Pause -> {
                        if (isTracking.value) {
                            onAction(
                                action = ActiveRunAction.OnToggleRunClick,
                                triggeredOnWatch = true
                            )
                        }
                    }
                    MessagingAction.StartOrResume -> {
                        if (!isTracking.value) {
                            if (state.hasStartedRunning) {
                                onAction(
                                    action = ActiveRunAction.OnResumeRunClick,
                                    triggeredOnWatch = true
                                )
                            } else {
                                onAction(
                                    action = ActiveRunAction.OnResumeRunClick,
                                    triggeredOnWatch = true
                                )
                            }
                        }
                    }
                    else -> Unit
                }
            }
            .launchIn(viewModelScope)
    }

    override fun onCleared() {
        super.onCleared()
        if (!ActiveRunService.isServiceActive.value) {
            applicationScope.launch {
                watchConnector.sendActionToWatch(MessagingAction.Untrackable)
            }
            runningTracker.stopObservingLocation()
        }
    }

}

