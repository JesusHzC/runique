package com.jesushzc.core.domain.run

import com.jesushzc.core.domain.util.DataError
import com.jesushzc.core.domain.util.EmptyDataResult
import kotlinx.coroutines.flow.Flow

interface RunRepository {
    fun getRuns(): Flow<List<Run>>
    suspend fun fetchRuns(): EmptyDataResult<DataError>
    suspend fun upsertRun(run: Run, mapPicture: ByteArray): EmptyDataResult<DataError>
    suspend fun deleteRun(id: RunId)
}