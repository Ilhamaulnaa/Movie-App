package com.ilham.data.remote

import okhttp3.Interceptor
import okhttp3.Response

class TmdbAuthInterceptor(private val credential: String) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val request = if (credential.startsWith(JWT_PREFIX)) {
            original.newBuilder()
                .header(HEADER_AUTHORIZATION, "Bearer $credential")
                .build()
        } else {
            val url = original.url.newBuilder()
                .addQueryParameter(QUERY_API_KEY, credential)
                .build()
            original.newBuilder().url(url).build()
        }
        return chain.proceed(request)
    }

    private companion object {
        const val JWT_PREFIX = "eyJ"
        const val HEADER_AUTHORIZATION = "Authorization"
        const val QUERY_API_KEY = "api_key"
    }
}