package com.jesushzc.run.presentation.active_run

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

class ActiveRunViewModel: ViewModel() {

    var state by mutableStateOf(ActiveRunState())
        private set

    private val _eventChannel = Channel<ActiveRunEvent>()
    val eventChannel = _eventChannel.receiveAsFlow()

    private val _hasLocationPermission = MutableStateFlow(false)
    val hasLocationPermission = _hasLocationPermission.asStateFlow()

    fun onAction(action: ActiveRunAction) {
        when (action) {
            ActiveRunAction.OnFinishRunClick -> {
                TODO()
            }
            ActiveRunAction.OnResumeRunClick -> {
                TODO()
            }
            ActiveRunAction.OnToggleRunClick -> {
                TODO()
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
            else -> Unit
        }
    }

}

