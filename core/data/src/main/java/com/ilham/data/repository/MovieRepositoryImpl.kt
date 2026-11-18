package com.ilham.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.filter
import com.ilham.common.DataResult
import com.ilham.data.remote.DtoMapper.toDomain
import com.ilham.data.remote.DtoMapper.toDomainOrNull
import com.ilham.data.remote.TmdbApi
import com.ilham.data.remote.dto.MovieDto
import com.ilham.data.remote.dto.ReviewDto
import com.ilham.data.remote.paging.TmdbPagingSource
import com.ilham.data.util.safeApiCall
import com.ilham.domain.model.Genre
import com.ilham.domain.model.Movie
import com.ilham.domain.model.MovieDetail
import com.ilham.domain.model.Review
import com.ilham.domain.model.Video
import com.ilham.domain.repository.MovieRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MovieRepositoryImpl @Inject constructor(
    private val api: TmdbApi
) : MovieRepository{

    override suspend fun getGenres(): DataResult<List<Genre>> = safeApiCall {
        api.getGenres().genres
            .map { it.toDomain() }
            .filter { it.name.isNotBlank() }
    }

    override fun getMovieByGenre(genreId: Int): Flow<PagingData<Movie>> = Pager<Int, Movie>(
        config = pagingConfig(),
        pagingSourceFactory = {
            TmdbPagingSource(
                fetchPage = { page -> api.discoverMovies(genreId = genreId, page = page) },
                mapper = { dto: MovieDto -> dto.toDomain() },
            )
        },
    ).flow.distinctItemsBy { it.id }

    override suspend fun getMovieDetail(movieId: Int): DataResult<MovieDetail> = safeApiCall {
        api.getMovieDetail(movieId).toDomain()
    }

    override suspend fun getMovieVideos(movieId: Int): DataResult<List<Video>> = safeApiCall {
        api.getMovieVideos(movieId).results.mapNotNull { it.toDomainOrNull() }
    }

    override fun getMovieReview(movieId: Int): Flow<PagingData<Review>> = Pager<Int, Review>(
        config = pagingConfig(),
        pagingSourceFactory = {
            TmdbPagingSource(
                fetchPage = { page -> api.getMovieReviews(movieId = movieId, page = page) },
                mapper = { dto: ReviewDto -> dto.toDomain() },
            )
        },
    ).flow.distinctItemsBy { it.id }

    private fun pagingConfig() = PagingConfig(
        pageSize = TMDB_PAGE_SIZE,
        initialLoadSize = TMDB_PAGE_SIZE,
        enablePlaceholders = false,
    )

    private fun <T : Any, K> Flow<PagingData<T>>.distinctItemsBy(selector: (T) -> K): Flow<PagingData<T>> =
        map { pagingData ->
            val seenKeys = HashSet<K>()
            pagingData.filter { seenKeys.add(selector(it)) }
        }

    private companion object {
        const val TMDB_PAGE_SIZE = 20
    }
}