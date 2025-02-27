package com.jesushz.core.connectivity.domain.messaging

import com.jesushzc.core.domain.util.EmptyDataResult
import kotlinx.coroutines.flow.Flow

interface MessagingClient {

    fun connectToDevice(deviceId: String): Flow<MessagingAction>
    suspend fun sendOrQueueAction(action: MessagingAction): EmptyDataResult<MessagingError>


}