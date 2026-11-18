package com.ilham.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ilham.common.AppError
import com.ilham.common.DataResult
import com.ilham.domain.model.MovieDetail
import com.ilham.domain.model.Review
import com.ilham.domain.model.Video
import com.ilham.domain.usecase.GetMovieDetailUseCase
import com.ilham.domain.usecase.GetMovieReviewsUseCase
import com.ilham.domain.usecase.GetMovieTrailerUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface MovieDetailUiState {
    data object Loading : MovieDetailUiState
    data class Error(val error: AppError) : MovieDetailUiState
    data class Success(val movie: MovieDetail, val trailer: TrailerState) : MovieDetailUiState
}

sealed interface TrailerState {
    data class Available(val video: Video) : TrailerState
    data object NotAvailable : TrailerState
    data class Failed(val error: AppError) : TrailerState
}

@HiltViewModel(assistedFactory = MovieDetailViewModel.Factory::class)
class MovieDetailViewModel @AssistedInject constructor(
    @Assisted private val movieId: Int,
    private val getMovieDetail: GetMovieDetailUseCase,
    private val getMovieTrailer: GetMovieTrailerUseCase,
    getMovieReviews: GetMovieReviewsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<MovieDetailUiState>(MovieDetailUiState.Loading)
    val uiState: StateFlow<MovieDetailUiState> = _uiState.asStateFlow()

    val reviews: Flow<PagingData<Review>> = getMovieReviews(movieId).cachedIn(viewModelScope)

    private var loadJob: Job? = null

    init {
        loadMovie()
    }

    fun loadMovie() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = MovieDetailUiState.Loading

            val trailerDeferred = async { getMovieTrailer(movieId) }
            _uiState.value = when (val detailResult = getMovieDetail(movieId)) {
                is DataResult.Error -> {
                    trailerDeferred.cancel()
                    MovieDetailUiState.Error(detailResult.error)
                }
                is DataResult.Success -> MovieDetailUiState.Success(
                    movie = detailResult.data,
                    trailer = trailerDeferred.await().toTrailerState(),
                )
            }
        }
    }

    private fun DataResult<Video?>.toTrailerState(): TrailerState = when (this) {
        is DataResult.Success -> data?.let { TrailerState.Available(it) } ?: TrailerState.NotAvailable
        is DataResult.Error -> TrailerState.Failed(error)
    }

    @AssistedFactory
    interface Factory {
        fun create(movieId: Int): MovieDetailViewModel
    }
}