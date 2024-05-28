package com.jesushzc.auth.data.repository

import com.jesushzc.auth.data.model.LoginRequest
import com.jesushzc.auth.data.model.LoginResponse
import com.jesushzc.auth.data.model.RegisterRequest
import com.jesushzc.core.data.networking.post
import com.jesushzc.auth.domain.repository.AuthRepository
import com.jesushzc.core.domain.AuthInfo
import com.jesushzc.core.domain.SessionStorage
import com.jesushzc.core.domain.util.DataError
import com.jesushzc.core.domain.util.EmptyDataResult
import com.jesushzc.core.domain.util.Result
import com.jesushzc.core.domain.util.asEmptyDataResult
import com.jesushzc.core.util.Constants.ENDPOINT_LOGIN
import com.jesushzc.core.util.Constants.ENDPOINT_REGISTER
import io.ktor.client.HttpClient

class AuthRepositoryImpl(
    private val httpClient: HttpClient,
    private val sessionStorage: SessionStorage
): AuthRepository {

    override suspend fun register(
        email: String,
        password: String
    ): EmptyDataResult<DataError.Network> {
        return httpClient.post<RegisterRequest, Unit>(
            route = ENDPOINT_REGISTER,
            body = RegisterRequest(
                email = email,
                password = password
            )
        )
    }

    override suspend fun login(
        email: String,
        password: String
    ): EmptyDataResult<DataError.Network> {
        val result = httpClient.post<LoginRequest, LoginResponse>(
            route = ENDPOINT_LOGIN,
            body = LoginRequest(
                email = email,
                password = password
            )
        )
        if (result is Result.Success) {
            sessionStorage.set(
                AuthInfo(
                    accessToken = result.data.accessToken,
                    refreshToken = result.data.refreshToken,
                    userId = result.data.userId
                )
            )
        }
        return result.asEmptyDataResult()
    }

}

