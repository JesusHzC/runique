package com.jesushzc.auth.data.repository

import com.jesushzc.auth.data.model.RegisterRequest
import com.jesushzc.core.data.networking.post
import com.jesushzc.auth.domain.repository.AuthRepository
import com.jesushzc.core.domain.util.DataError
import com.jesushzc.core.domain.util.EmptyDataResult
import io.ktor.client.HttpClient

class AuthRepositoryImpl(
    private val httpClient: HttpClient
): AuthRepository {

    override suspend fun register(
        email: String,
        password: String
    ): EmptyDataResult<DataError.Network> {
        return httpClient.post<RegisterRequest, Unit>(
            route = "/register",
            body = RegisterRequest(
                email = email,
                password = password
            )
        )
    }

}

