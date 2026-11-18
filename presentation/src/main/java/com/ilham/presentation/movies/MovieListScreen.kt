package com.ilham.presentation.movies

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.ilham.domain.model.Movie
import com.ilham.presentation.R
import com.ilham.presentation.common.BackButton
import com.ilham.presentation.common.EmptyState
import com.ilham.presentation.common.ErrorState
import com.ilham.presentation.common.LoadingState
import com.ilham.presentation.common.PagingAppendFooter
import com.ilham.presentation.common.TestTags
import com.ilham.presentation.common.asAppError
import com.ilham.presentation.common.messageRes

@Composable
fun MovieListScreen(genreId: Int, genreName: String, onMovieClick: (Movie) -> Unit, onBack: () -> Unit) {
    val viewModel = hiltViewModel<MovieListViewModel, MovieListViewModel.Factory>(
        key = "movies_$genreId",
        creationCallback = { factory -> factory.create(genreId) },
    )
    val movies = viewModel.movies.collectAsLazyPagingItems()

    MovieListContent(
        title = genreName,
        movies = movies,
        onMovieClick = onMovieClick,
        onBack = onBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieListContent(
    title: String,
    movies: LazyPagingItems<Movie>,
    onMovieClick: (Movie) -> Unit,
    onBack: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val refreshState = movies.loadState.refresh
    val isEmpty = movies.itemCount == 0

    val refreshErrorMessage = (refreshState as? LoadState.Error)
        ?.takeIf { !isEmpty }
        ?.let { stringResource(it.error.asAppError().messageRes()) }
    LaunchedEffect(refreshErrorMessage) {
        refreshErrorMessage?.let { snackbarHostState.showSnackbar(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = { BackButton(onBack) },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when {
                refreshState is LoadState.Loading && isEmpty -> LoadingState()

                refreshState is LoadState.Error && isEmpty -> ErrorState(
                    error = refreshState.error.asAppError(),
                    onRetry = movies::retry,
                )

                refreshState is LoadState.NotLoading &&
                        movies.loadState.append.endOfPaginationReached &&
                        isEmpty -> EmptyState(message = stringResource(R.string.empty_movies))

                else -> PullToRefreshBox(
                    isRefreshing = refreshState is LoadState.Loading,
                    onRefresh = movies::refresh,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    MovieGrid(movies = movies, onMovieClick = onMovieClick)
                }
            }
        }
    }
}

@Composable
private fun MovieGrid(movies: LazyPagingItems<Movie>, onMovieClick: (Movie) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 150.dp),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxSize()
            .testTag(TestTags.MOVIE_GRID),
    ) {
        items(
            count = movies.itemCount,
            key = movies.itemKey { it.id },
            contentType = movies.itemContentType { "movie" },
        ) { index ->
            movies[index]?.let { movie ->
                MovieCard(movie = movie, onClick = { onMovieClick(movie) })
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            PagingAppendFooter(
                loadState = movies.loadState.append,
                onRetry = movies::retry,
                showEndMessage = movies.itemCount > 0,
            )
        }
    }
}