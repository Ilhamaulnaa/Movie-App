package com.ilham.presentation.genre

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ilham.domain.model.Genre
import com.ilham.presentation.R
import com.ilham.presentation.common.EmptyState
import com.ilham.presentation.common.ErrorState
import com.ilham.presentation.common.LoadingState
import com.ilham.presentation.common.TestTags

@Composable
fun GenreScreen(
    onGenreClick: (Genre) -> Unit,
    viewModel: GenreViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    GenreContent(
        uiState = uiState,
        onGenreClick = onGenreClick,
        onRetry = viewModel::loadGenres,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenreContent(
    uiState: GenreUiState,
    onGenreClick: (Genre) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(stringResource(R.string.title_genres))
                        Text(
                            text = stringResource(R.string.subtitle_genres),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when (uiState) {
                GenreUiState.Loading -> LoadingState()
                GenreUiState.Empty -> EmptyState(message = stringResource(R.string.empty_genres))
                is GenreUiState.Error -> ErrorState(error = uiState.error, onRetry = onRetry)
                is GenreUiState.Success -> GenreGrid(genres = uiState.genres, onGenreClick = onGenreClick)
            }
        }
    }
}

@Composable
private fun GenreGrid(genres: List<Genre>, onGenreClick: (Genre) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxSize()
            .testTag(TestTags.GENRE_GRID),
    ) {
        items(items = genres, key = { it.id }) { genre ->
            GenreCard(genre = genre, onClick = { onGenreClick(genre) })
        }
    }
}

@Composable
private fun GenreCard(genre: Genre, onClick: () -> Unit) {
    val colors = GenreGradients[genre.id.mod(GenreGradients.size)]
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(88.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(colors))
                .padding(16.dp),
            contentAlignment = Alignment.BottomStart,
        ) {
            Text(
                text = genre.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
        }
    }
}

private val GenreGradients = listOf(
    listOf(Color(0xFF003D79), Color(0xFF1E88E5)),
    listOf(Color(0xFFB71C1C), Color(0xFFEF5350)),
    listOf(Color(0xFF1B5E20), Color(0xFF43A047)),
    listOf(Color(0xFF4A148C), Color(0xFF8E24AA)),
    listOf(Color(0xFFE65100), Color(0xFFFFA726)),
    listOf(Color(0xFF006064), Color(0xFF26C6DA)),
    listOf(Color(0xFF3E2723), Color(0xFF8D6E63)),
    listOf(Color(0xFF263238), Color(0xFF78909C)),
)

@Preview(showBackground = true)
@Composable
private fun GenreContentPreview() {
    MaterialTheme {
        GenreContent(
            uiState = GenreUiState.Success(
                listOf(
                    Genre(28, "Action"),
                    Genre(12, "Adventure"),
                    Genre(16, "Animation"),
                    Genre(35, "Comedy")
                )
            ),
            onGenreClick = {},
            onRetry = {},
        )
    }
}