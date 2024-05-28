package com.jesushzc.run.presentation.active_run

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class ActiveRunViewModel: ViewModel() {

    var state by mutableStateOf(ActiveRunState())
        private set

    private val _eventChannel = Channel<ActiveRunEvent>()
    val eventChannel = _eventChannel.receiveAsFlow()

    fun onAction(action: ActiveRunAction) {

    }

}

