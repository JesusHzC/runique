package com.jesushzc.core.domain.util

import com.jesushzc.core.domain.util.Error as UtilError

sealed interface Result<out D, out E: UtilError> {
    data class Success<out D>(val data: D): Result<D, Nothing>
    data class Error<out E: UtilError>(val error: E): Result<Nothing, E>
}

inline fun <T, E: UtilError, R> Result<T, E>.map(map: (T) -> R): Result<R, E> {
    return when(this) {
        is Result.Error -> Result.Error(error)
        is Result.Success -> Result.Success(map(data))
    }
}

fun <T, E: UtilError> Result<T, E>.asEmptyDataResult(): EmptyDataResult<E> {
    return map {  }
}

typealias EmptyDataResult<E> = Result<Unit, E>

