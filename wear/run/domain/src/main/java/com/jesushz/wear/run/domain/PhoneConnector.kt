package com.jesushz.wear.run.domain

import com.jesushz.core.connectivity.domain.DeviceNode
import kotlinx.coroutines.flow.StateFlow

interface PhoneConnector {

    val connectedDevice: StateFlow<DeviceNode?>

}