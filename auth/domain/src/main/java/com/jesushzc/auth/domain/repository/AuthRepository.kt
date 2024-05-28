package com.jesushzc.auth.domain.repository

import com.jesushzc.core.domain.util.DataError
import com.jesushzc.core.domain.util.EmptyDataResult

interface AuthRepository {
    suspend fun register(
        email: String,
        password: String
    ): EmptyDataResult<DataError.Network>

    suspend fun login(
        email: String,
        password: String
    ): EmptyDataResult<DataError.Network>
}

