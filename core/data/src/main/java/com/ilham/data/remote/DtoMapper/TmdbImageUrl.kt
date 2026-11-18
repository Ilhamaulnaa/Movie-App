package com.ilham.data.remote.DtoMapper

import com.ilham.domain.BuildConfig

object TmdbImageUrl {
    private const val POSTER_SIZE = "w500"
    private const val BACKDROP_SIZE = "w780"
    private const val AVATAR_SIZE = "w185"

    fun poster(path: String?): String? = build(path, POSTER_SIZE)

    fun backdrop(path: String?): String? = build(path, BACKDROP_SIZE)

    fun avatar(path: String?): String? {
        if (path.isNullOrBlank()) return null
        return if (path.startsWith("/http")) path.removePrefix("/") else build(path, AVATAR_SIZE)
    }

    private fun build(path: String?, size: String): String? =
        path?.takeIf { it.isNotBlank() }?.let { "${BuildConfig.TMDB_IMAGE_BASE_URL}$size$it" }
}