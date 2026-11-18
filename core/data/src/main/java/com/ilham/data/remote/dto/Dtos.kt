package com.ilham.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PagedResponse<T>(
    val page: Int = 1,
    val results: List<T> = emptyList(),
    @SerialName("total_pages") val totalPages: Int = 0,
    @SerialName("total_results") val totalResults: Int = 0,
)

@Serializable
data class GenreListResponse(val genres: List<GenreDto> = emptyList())

@Serializable
data class GenreDto(val id: Int, val name: String? = null)

@Serializable
data class MovieDto(
    val id: Int,
    val title: String? = null,
    val overview: String? = null,
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("release_date") val releaseDate: String? = null,
    @SerialName("vote_average") val voteAverage: Double? = null,
)

@Serializable
data class MovieDetailDto(
    val id: Int,
    val title: String? = null,
    val tagline: String? = null,
    val overview: String? = null,
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("backdrop_path") val backdropPath: String? = null,
    @SerialName("release_date") val releaseDate: String? = null,
    val runtime: Int? = null,
    @SerialName("vote_average") val voteAverage: Double? = null,
    @SerialName("vote_count") val voteCount: Int? = null,
    val genres: List<GenreDto> = emptyList(),
)

@Serializable
data class ReviewDto(
    val id: String,
    val author: String? = null,
    @SerialName("author_details") val authorDetails: AuthorDetailsDto? = null,
    val content: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
)

@Serializable
data class AuthorDetailsDto(
    val name: String? = null,
    val username: String? = null,
    @SerialName("avatar_path") val avatarPath: String? = null,
    val rating: Double? = null,
)

@Serializable
data class VideoListResponse(val id: Int? = null, val results: List<VideoDto> = emptyList())

@Serializable
data class VideoDto(
    val key: String? = null,
    val name: String? = null,
    val site: String? = null,
    val type: String? = null,
    val official: Boolean? = null,
)