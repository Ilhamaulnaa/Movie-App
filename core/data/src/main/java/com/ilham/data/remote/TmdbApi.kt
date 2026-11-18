package com.ilham.data.remote

import com.ilham.data.remote.dto.GenreListResponse
import com.ilham.data.remote.dto.MovieDetailDto
import com.ilham.data.remote.dto.MovieDto
import com.ilham.data.remote.dto.PagedResponse
import com.ilham.data.remote.dto.ReviewDto
import com.ilham.data.remote.dto.VideoListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApi {

    @GET("genre/movie/list")
    suspend fun getGenres(): GenreListResponse

    @GET("discover/movie")
    suspend fun discoverMovies(
        @Query("with_genres") genreId: Int,
        @Query("page") page: Int,
        @Query("sort_by") sortBy: String = "popularity.desc",
        @Query("include_adult") includeAdult: Boolean = false,
    ): PagedResponse<MovieDto>

    @GET("movie/{movie_id}")
    suspend fun getMovieDetail(@Path("movie_id") movieId: Int): MovieDetailDto

    @GET("movie/{movie_id}/reviews")
    suspend fun getMovieReviews(@Path("movie_id") movieId: Int, @Query("page") page: Int): PagedResponse<ReviewDto>

    @GET("movie/{movie_id}/videos")
    suspend fun getMovieVideos(@Path("movie_id") movieId: Int): VideoListResponse
}