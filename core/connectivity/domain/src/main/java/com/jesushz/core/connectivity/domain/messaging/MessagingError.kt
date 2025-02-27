package com.jesushz.core.connectivity.domain.messaging

import com.jesushzc.core.domain.util.Error

enum class MessagingError: Error {
    CONNECTION_INTERRUPTED,
    DISCONNECTED,
    UNKNOWN
}
