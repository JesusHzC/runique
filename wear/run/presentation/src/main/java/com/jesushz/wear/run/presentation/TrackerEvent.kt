package com.jesushz.wear.run.presentation

sealed interface TrackerEvent {

    data object RunFinished: TrackerEvent

}
