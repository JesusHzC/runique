package com.jesushzc.core.domain.run

import com.jesushzc.core.domain.util.DataError
import kotlinx.coroutines.flow.Flow
import com.jesushzc.core.domain.util.Result as ResultCore

typealias RunId = String

interface LocalRunDataSource {
    fun getRuns(): Flow<List<Run>>
    suspend fun upsertRun(run: Run): ResultCore<RunId, DataError.Local>
    suspend fun upsertRuns(runs: List<Run>): ResultCore<List<RunId>, DataError.Local>
    suspend fun deleteRun(id: String)
    suspend fun deleteAllRuns()
}
