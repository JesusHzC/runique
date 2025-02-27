package com.jesushz.wear.run.presentation

import com.jesushzc.core.presentation.ui.UiText

sealed interface TrackerEvent {

    data object RunFinished: TrackerEvent
    data class Error(val uiText: UiText): TrackerEvent

}
