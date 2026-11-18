package com.ilham.navigation

import kotlinx.serialization.Serializable

@Serializable
data object GenreDestination

@Serializable
data class MovieListDestination(val genreId: Int, val genreName: String)

@Serializable
data class MovieDetailDestination(val movieId: Int, val title: String)