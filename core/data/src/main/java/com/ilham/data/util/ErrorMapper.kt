package com.ilham.data.util

import com.ilham.common.AppError
import com.ilham.common.DataResult
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import kotlin.coroutines.cancellation.CancellationException

private const val HTTP_UNAUTHORIZED = 401
private const val HTTP_NOT_FOUND = 404
private val HTTP_SERVER_ERRORS = 500..599

fun Throwable.toAppError(): AppError = when (this) {
    is AppError -> this
    is SocketTimeoutException -> AppError.Timeout
    is IOException -> AppError.NoInternet
    is HttpException -> when (code()) {
        HTTP_UNAUTHORIZED -> AppError.Unauthorized
        HTTP_NOT_FOUND -> AppError.NotFound
        in HTTP_SERVER_ERRORS -> AppError.Server(code())
        else -> AppError.Unknown("HTTP ${code()}")
    }
    is SerializationException -> AppError.Unknown("Unexpected response format")
    else -> AppError.Unknown(message)
}

suspend fun <T> safeApiCall(block: suspend () -> T): DataResult<T> = try {
    DataResult.Success(block())
} catch (e: CancellationException) {
    throw e
} catch (e: Exception) {
    DataResult.Error(e.toAppError())
}