package com.ilham.domain.usecase

import androidx.paging.PagingData
import com.ilham.common.DataResult
import com.ilham.domain.model.Genre
import com.ilham.domain.model.Movie
import com.ilham.domain.model.MovieDetail
import com.ilham.domain.model.Review
import com.ilham.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGenresUseCase @Inject constructor(private val repository: MovieRepository) {
    suspend operator fun invoke(): DataResult<List<Genre>> = repository.getGenres()
}

class GetMoviesByGenreUseCase @Inject constructor(private val repository: MovieRepository) {
    operator fun invoke(genreId: Int): Flow<PagingData<Movie>> = repository.getMovieByGenre(genreId)
}

class GetMovieDetailUseCase @Inject constructor(private val repository: MovieRepository) {
    suspend operator fun invoke(movieId: Int): DataResult<MovieDetail> = repository.getMovieDetail(movieId)
}

class GetMovieReviewsUseCase @Inject constructor(private val repository: MovieRepository) {
    operator fun invoke(movieId: Int): Flow<PagingData<Review>> = repository.getMovieReview(movieId)
}