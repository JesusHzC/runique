package com.jesushz.wear.run.data

import com.jesushz.core.connectivity.domain.DeviceNode
import com.jesushz.core.connectivity.domain.DeviceType
import com.jesushz.core.connectivity.domain.NodeDiscovery
import com.jesushz.core.connectivity.domain.messaging.MessagingAction
import com.jesushz.core.connectivity.domain.messaging.MessagingClient
import com.jesushz.core.connectivity.domain.messaging.MessagingError
import com.jesushz.wear.run.domain.PhoneConnector
import com.jesushzc.core.domain.util.EmptyDataResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn

class WatchToPhoneConnector(
    nodeDiscovery: NodeDiscovery,
    applicationScope: CoroutineScope,
    private val messagingClient: MessagingClient
): PhoneConnector {

    private val _connectedNode = MutableStateFlow<DeviceNode?>(null)
    override val connectedDevice = _connectedNode.asStateFlow()

    override val messagingActions = nodeDiscovery
        .observeConnectedDevices(DeviceType.WATCH)
        .flatMapLatest { connectedDevices ->
            val node = connectedDevices.firstOrNull()
            if (node != null && node.isNearby) {
                _connectedNode.value = node
                messagingClient.connectToDevice(node.id)
            } else flowOf()
        }
        .shareIn(
            scope = applicationScope,
            started = SharingStarted.Eagerly
        )

    override suspend fun sendActionToPhone(action: MessagingAction): EmptyDataResult<MessagingError> {
        return messagingClient.sendOrQueueAction(action)
    }

}
