package com.ilham.common

sealed class AppError(message: String? = null) : Exception(message) {
    data object NoInternet : AppError("No internet connection")
    data object Timeout : AppError("Request timed out")
    data object Unauthorized : AppError("Invalid or missing API key")
    data object NotFound : AppError("Resource not found")
    data class Server(val code: Int) : AppError("Server error ($code)")
    data class Unknown(val detail: String? = null) : AppError(detail)
}