package com.ilham.presentation.movies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ilham.domain.model.Movie
import com.ilham.domain.usecase.GetMoviesByGenreUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow

@HiltViewModel(assistedFactory = MovieListViewModel.Factory::class)
class MovieListViewModel @AssistedInject constructor(
    @Assisted genreId: Int,
    getMoviesByGenre: GetMoviesByGenreUseCase,
) : ViewModel() {

    val movies: Flow<PagingData<Movie>> = getMoviesByGenre(genreId).cachedIn(viewModelScope)

    @AssistedFactory
    interface Factory {
        fun create(genreId: Int): MovieListViewModel
    }
}