package com.ilham.data.remote.DtoMapper

import com.ilham.data.remote.dto.GenreDto
import com.ilham.data.remote.dto.MovieDetailDto
import com.ilham.data.remote.dto.MovieDto
import com.ilham.data.remote.dto.ReviewDto
import com.ilham.data.remote.dto.VideoDto
import com.ilham.domain.model.Genre
import com.ilham.domain.model.Movie
import com.ilham.domain.model.MovieDetail
import com.ilham.domain.model.Review
import com.ilham.domain.model.Video

private const val ANONYMOUS_AUTHOR = "Anonymous"

internal fun GenreDto.toDomain(): Genre = Genre(
    id = id,
    name = name.orEmpty()
)

internal fun MovieDto.toDomain(): Movie = Movie(
    id = id,
    title = title.orEmpty(),
    overview = overview.orEmpty(),
    posterUrl = TmdbImageUrl.poster(posterPath),
    releaseDate = releaseDate,
    rating = voteAverage ?: 0.0
)

internal fun MovieDetailDto.toDomain(): MovieDetail = MovieDetail(
    id = id,
    title = title.orEmpty(),
    tagline = tagline,
    overview = overview.orEmpty(),
    posterUrl = TmdbImageUrl.poster(posterPath),
    backdropUrl = TmdbImageUrl.backdrop(backdropPath),
    releaseDate = releaseDate,
    runtimeMinutes = runtime,
    rating = voteAverage ?: 0.0,
    voteCount = voteCount ?: 0,
    genres = genres.map { it.toDomain() }
)

internal fun ReviewDto.toDomain(): Review = Review(
    id = id,
    author = author?.takeIf { it.isNotBlank() }
        ?: authorDetails?.username?.takeIf { it.isNotBlank() }
        ?: ANONYMOUS_AUTHOR,
    avatarUrl = TmdbImageUrl.avatar(authorDetails?.avatarPath),
    rating = authorDetails?.rating,
    content = content.orEmpty().trim(),
    createdAt = createdAt,
)

internal fun VideoDto.toDomainOrNull(): Video? {
    val videoKey = key?.takeIf { it.isNotBlank() } ?: return null
    return Video(
        key = videoKey,
        name = name.orEmpty(),
        site = site.orEmpty(),
        type = type.orEmpty(),
        isOfficial = official ?: false,
    )
}