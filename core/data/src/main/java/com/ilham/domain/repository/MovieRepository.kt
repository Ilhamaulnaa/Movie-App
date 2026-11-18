package com.ilham.domain.repository

import androidx.paging.PagingData
import com.ilham.common.DataResult
import com.ilham.domain.model.Genre
import com.ilham.domain.model.Movie
import com.ilham.domain.model.MovieDetail
import com.ilham.domain.model.Review
import com.ilham.domain.model.Video
import kotlinx.coroutines.flow.Flow

interface MovieRepository {

    suspend fun getGenres(): DataResult<List<Genre>>

    fun getMovieByGenre(genreId: Int): Flow<PagingData<Movie>>

    suspend fun getMovieDetail(movieId: Int): DataResult<MovieDetail>

    suspend fun getMovieVideos(movieId: Int): DataResult<List<Video>>

    fun getMovieReview(movieId: Int): Flow<PagingData<Review>>

}