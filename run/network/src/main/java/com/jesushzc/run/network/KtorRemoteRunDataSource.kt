package com.jesushzc.run.network

import com.jesushzc.core.data.networking.constructRoute
import com.jesushzc.core.data.networking.delete
import com.jesushzc.core.data.networking.get
import com.jesushzc.core.data.networking.safeCall
import com.jesushzc.core.domain.run.RemoteRunDataSource
import com.jesushzc.core.domain.run.Run
import com.jesushzc.core.domain.util.DataError
import com.jesushzc.core.domain.util.EmptyDataResult
import com.jesushzc.core.domain.util.Result
import com.jesushzc.core.domain.util.map
import com.jesushzc.core.util.Constants.ENDPOINT_RUN
import com.jesushzc.core.util.Constants.ENDPOINT_RUNS
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class KtorRemoteRunDataSource(
    private val httpClient: HttpClient
): RemoteRunDataSource {
    override suspend fun getRuns(): Result<List<Run>, DataError.Network> {
        return httpClient.get<List<RunDto>>(
            route = ENDPOINT_RUNS,
        ).map { runDtos ->
            runDtos.map {
                it.toRun()
            }
        }
    }

    override suspend fun postRun(run: Run, mapPicture: ByteArray): Result<Run, DataError.Network> {
        val createRunRequest = Json.encodeToString(run.toCreateRunRequest())
        val response = safeCall<RunDto> {
            httpClient.submitFormWithBinaryData(
                url = constructRoute(ENDPOINT_RUN),
                formData = formData {
                    append(
                        "MAP_PICTURE",
                        mapPicture,
                        Headers.build {
                            append(HttpHeaders.ContentType, "image/jpeg")
                            append(HttpHeaders.ContentDisposition, "filename=mappicture.jpg")
                        }
                    )
                    append(
                        "RUN_DATA",
                        createRunRequest,
                        Headers.build {
                            append(HttpHeaders.ContentType, "text/plain")
                            append(HttpHeaders.ContentDisposition, "form-data; name=\"RUN_DATA\"")
                        }
                    )
                }
            ) {
                method = HttpMethod.Post
            }
        }
        return response.map { it.toRun() }
    }

    override suspend fun deleteRun(id: String): EmptyDataResult<DataError.Network> {
        return httpClient.delete(
            route = ENDPOINT_RUN,
            queryParameters = mapOf("id" to id)
        )
    }

}
