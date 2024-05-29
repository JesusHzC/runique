@file:OptIn(ExperimentalCoroutinesApi::class)

package com.jesushzc.run.domain

import com.jesushzc.core.domain.Timer
import com.jesushzc.core.domain.location.LocationTimestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.zip
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class RunningTracker(
    private val locationObserver: LocationObserver,
    private val applicationScope: CoroutineScope
) {

    private val _runData = MutableStateFlow(RunData())
    val runData = _runData.asStateFlow()

    private val isTracking = MutableStateFlow(false)
    private val isObservingLocation = MutableStateFlow(false)

    private val _elapsedTime = MutableStateFlow(Duration.ZERO)
    val elapsedTime = _elapsedTime.asStateFlow()

    val currentLocation = isObservingLocation
        .flatMapLatest { isObserving ->
            if (isObserving) {
                locationObserver.observeLocation(1000L)
            } else flowOf()
        }
        .stateIn(
            applicationScope,
            SharingStarted.Lazily,
            null
        )

    init {
        isTracking
            .onEach { isTracking ->
                if (isTracking) {
                    val newList = buildList {
                        addAll(runData.value.locations)
                        add(emptyList<LocationTimestamp>())
                    }.toList()
                    _runData.update {
                        it.copy(
                            locations = newList
                        )
                    }
                }
            }
            .flatMapLatest { isTracking ->
                if (isTracking) {
                    Timer.timeAndEmit()
                } else flowOf()
            }
            .onEach { duration ->
                _elapsedTime.value += duration
            }
            .launchIn(applicationScope)

        currentLocation
            .filterNotNull()
            .combineTransform(isTracking) { location, isTracking ->
                if (isTracking) {
                    emit(location)
                }
            }
            .zip(_elapsedTime) { location, elapsedTime ->
                LocationTimestamp(
                    locationWithAltitude = location,
                    durationTimestamp = elapsedTime
                )
            }
            .onEach { locationTimestamp ->
                val currentLocations = runData.value.locations
                val lastLocationsList = if(currentLocations.isNotEmpty()) {
                    currentLocations.last() + locationTimestamp
                } else listOf(locationTimestamp)
                val newLocationsList = currentLocations.replaceLast(lastLocationsList)

                val distanceMeters = LocationDataCalculator.getTotalDistanceMeters(
                    locations = newLocationsList
                )
                val distanceKm = distanceMeters / 1000.0
                val currentDuration = locationTimestamp.durationTimestamp

                val avgSecondsPerKm = if(distanceKm == 0.0) {
                    0
                } else {
                    (currentDuration.inWholeSeconds / distanceKm).roundToInt()
                }

                _runData.update {
                    RunData(
                        distanceMeters = distanceMeters,
                        pace = avgSecondsPerKm.seconds,
                        locations = newLocationsList
                    )
                }
            }
            .launchIn(applicationScope)
    }

    fun setIsTracking(isTracking: Boolean) {
        this.isTracking.value = isTracking
    }

    fun startObservingLocation() {
        isObservingLocation.value = true
    }

    fun stopObservingLocation() {
        isObservingLocation.value = false
    }

}

private fun <T> List<List<T>>.replaceLast(replacement: List<T>): List<List<T>> {
    if (isEmpty()) return listOf(replacement)
    return dropLast(1) + listOf(replacement)
}

