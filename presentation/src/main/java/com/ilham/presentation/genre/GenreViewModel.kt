package com.ilham.presentation.genre

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ilham.common.AppError
import com.ilham.common.DataResult
import com.ilham.domain.model.Genre
import com.ilham.domain.usecase.GetGenresUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface GenreUiState {
    data object Loading : GenreUiState
    data object Empty : GenreUiState
    data class Success(val genres: List<Genre>) : GenreUiState
    data class Error(val error: AppError) : GenreUiState
}

@HiltViewModel
class GenreViewModel @Inject constructor(private val getGenres: GetGenresUseCase) : ViewModel() {

    private val _uiState = MutableStateFlow<GenreUiState>(GenreUiState.Loading)
    val uiState: StateFlow<GenreUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        loadGenres()
    }

    fun loadGenres() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = GenreUiState.Loading
            _uiState.value = when (val result = getGenres()) {
                is DataResult.Success -> if (result.data.isEmpty()) {
                    GenreUiState.Empty
                } else {
                    GenreUiState.Success(result.data)
                }
                is DataResult.Error -> GenreUiState.Error(result.error)
            }
        }
    }
}