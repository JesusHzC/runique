package com.jesushzc.run.presentation.run_overview

import androidx.lifecycle.ViewModel

class RunOverviewViewModel: ViewModel() {

    fun onAction(action: RunOverviewAction) {
        when(action) {
            RunOverviewAction.OnStartClick -> {
                // Handle start click
            }
            RunOverviewAction.OnLogoutClick -> {
                // Handle logout click
            }
            RunOverviewAction.OnAnalyticsClick -> {
                // Handle analytics click
            }
        }
    }

}
