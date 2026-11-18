package com.ilham.presentation.common

import androidx.annotation.StringRes
import com.ilham.common.AppError
import com.ilham.presentation.R

@StringRes
fun AppError.messageRes(): Int = when (this) {
    AppError.NoInternet -> R.string.error_no_internet
    AppError.Timeout -> R.string.error_timeout
    AppError.Unauthorized -> R.string.error_unauthorized
    AppError.NotFound -> R.string.error_not_found
    is AppError.Server -> R.string.error_server
    is AppError.Unknown -> R.string.error_unknown
}

fun Throwable.asAppError(): AppError = this as? AppError ?: AppError.Unknown(message)
