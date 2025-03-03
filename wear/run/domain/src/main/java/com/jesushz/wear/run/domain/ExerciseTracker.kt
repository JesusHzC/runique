package com.jesushz.wear.run.domain

import com.jesushzc.core.domain.util.EmptyDataResult
import com.jesushzc.core.domain.util.Error
import kotlinx.coroutines.flow.Flow

interface ExerciseTracker {

    val heartRate: Flow<Int>
    suspend fun isHeartRateTrackingSupported(): Boolean
    suspend fun prepareExercise(): EmptyDataResult<ExerciseError>
    suspend fun startExercise(): EmptyDataResult<ExerciseError>
    suspend fun resumeExercise(): EmptyDataResult<ExerciseError>
    suspend fun pauseExercise(): EmptyDataResult<ExerciseError>
    suspend fun stopExercise(): EmptyDataResult<ExerciseError>

}

enum class ExerciseError: Error {
    TRACKING_NOT_SUPPORTED,
    ONGOING_OWN_EXERCISE,
    ONGOING_OTHER_EXERCISE,
    EXERCISE_ALREADY_ENDED,
    UNKNOWN
}
