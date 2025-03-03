package com.jesushzc.run.domain

import com.jesushz.core.connectivity.domain.DeviceNode
import com.jesushz.core.connectivity.domain.messaging.MessagingAction
import com.jesushz.core.connectivity.domain.messaging.MessagingError
import com.jesushzc.core.domain.util.EmptyDataResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface WatchConnector {

    val connectedDevice: StateFlow<DeviceNode?>
    val messagingActions: Flow<MessagingAction>

    suspend fun sendActionToWatch(action: MessagingAction): EmptyDataResult<MessagingError>
    fun setIsTrackable(isTrackable: Boolean)

}
