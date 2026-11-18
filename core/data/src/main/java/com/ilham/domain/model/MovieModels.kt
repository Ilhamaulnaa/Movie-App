package com.ilham.domain.model

data class Genre(val id: Int, val name: String)

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val posterUrl: String?,
    val releaseDate: String?,
    val rating: Double,
)

data class MovieDetail(
    val id: Int,
    val title: String,
    val tagline: String?,
    val overview: String,
    val posterUrl: String?,
    val backdropUrl: String?,
    val releaseDate: String?,
    val runtimeMinutes: Int?,
    val rating: Double,
    val voteCount: Int,
    val genres: List<Genre>,
)

data class Review(
    val id: String,
    val author: String,
    val avatarUrl: String?,
    val rating: Double?,
    val content: String,
    val createdAt: String?,
)

data class Video(val key: String, val name: String, val site: String, val type: String, val isOfficial: Boolean) {
    val youtubeUrl: String get() = "https://www.youtube.com/watch?v=$key"
}
