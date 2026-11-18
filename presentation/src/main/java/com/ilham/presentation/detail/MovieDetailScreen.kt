package com.ilham.presentation.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import com.ilham.common.AppError
import com.ilham.domain.model.MovieDetail
import com.ilham.domain.model.Review
import com.ilham.presentation.R
import com.ilham.presentation.common.BackButton
import com.ilham.presentation.common.ErrorState
import com.ilham.presentation.common.Formatters
import com.ilham.presentation.common.LoadingState
import com.ilham.presentation.common.PagingAppendFooter
import com.ilham.presentation.common.TestTags
import com.ilham.presentation.common.asAppError
import com.ilham.presentation.common.messageRes

@Composable
fun MovieDetailScreen(movieId: Int, title: String, onBack: () -> Unit) {
    val viewModel = hiltViewModel<MovieDetailViewModel, MovieDetailViewModel.Factory>(
        key = "detail_$movieId",
        creationCallback = { factory -> factory.create(movieId) },
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val reviews = viewModel.reviews.collectAsLazyPagingItems()

    MovieDetailContent(
        title = title,
        uiState = uiState,
        reviews = reviews,
        onBack = onBack,
        onRetry = {
            viewModel.loadMovie()
            reviews.refresh()
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailContent(
    title: String,
    uiState: MovieDetailUiState,
    reviews: LazyPagingItems<Review>,
    onBack: () -> Unit,
    onRetry: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = { BackButton(onBack) },
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when (uiState) {
                MovieDetailUiState.Loading -> LoadingState()
                is MovieDetailUiState.Error -> ErrorState(error = uiState.error, onRetry = onRetry)
                is MovieDetailUiState.Success -> MovieDetailBody(
                    movie = uiState.movie,
                    trailer = uiState.trailer,
                    reviews = reviews,
                )
            }
        }
    }
}

@Composable
private fun MovieDetailBody(movie: MovieDetail, trailer: TrailerState, reviews: LazyPagingItems<Review>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag(TestTags.DETAIL_LIST),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        item(key = "header") { DetailHeader(movie) }
        item(key = "genres") { GenreChips(movie) }
        item(key = "overview") { OverviewSection(movie.overview) }
        item(key = "trailer") { TrailerSection(trailer) }
        item(key = "reviews_title") {
            SectionTitle(
                text = stringResource(R.string.section_reviews),
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
            )
        }
        reviewItems(reviews)
    }
}

@Composable
private fun DetailHeader(movie: MovieDetail) {
    Column {
        Box {
            AsyncImage(
                model = movie.backdropUrl ?: movie.posterUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            0.5f to Color.Transparent,
                            1f to MaterialTheme.colorScheme.background,
                        ),
                    ),
            )
        }

        Row(modifier = Modifier.padding(16.dp)) {
            val placeholder = painterResource(R.drawable.ic_movie_placeholder)
            AsyncImage(
                model = movie.posterUrl,
                contentDescription = movie.title,
                contentScale = ContentScale.Crop,
                placeholder = placeholder,
                error = placeholder,
                fallback = placeholder,
                modifier = Modifier
                    .width(110.dp)
                    .aspectRatio(2f / 3f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
            )
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
                movie.tagline?.let { tagline ->
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = tagline,
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(Modifier.height(12.dp))
                RatingRow(rating = movie.rating, voteCount = movie.voteCount)
                Spacer(Modifier.height(8.dp))
                val meta = listOfNotNull(
                    Formatters.releaseDate(movie.releaseDate),
                    Formatters.runtime(movie.runtimeMinutes),
                ).joinToString(separator = "  •  ")
                if (meta.isNotEmpty()) {
                    Text(
                        text = meta,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun RatingRow(rating: Double, voteCount: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = null,
            tint = Color.Yellow,
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = Formatters.rating(rating),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = stringResource(R.string.votes_count, Formatters.count(voteCount)),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun GenreChips(movie: MovieDetail) {
    if (movie.genres.isEmpty()) return
    FlowRow(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        movie.genres.forEach { genre ->
            Surface(
                shape = RoundedCornerShape(50),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            ) {
                Text(
                    text = genre.name,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                )
            }
        }
    }
}

@Composable
private fun OverviewSection(overview: String) {
    Column(modifier = Modifier.padding(16.dp)) {
        SectionTitle(text = stringResource(R.string.section_overview))
        Spacer(Modifier.height(8.dp))
        Text(
            text = overview.ifBlank { stringResource(R.string.empty_overview) },
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun TrailerSection(trailer: TrailerState) {
    val uriHandler = LocalUriHandler.current
    Column(modifier = Modifier.padding(16.dp)) {
        SectionTitle(text = stringResource(R.string.section_trailer))
        Spacer(Modifier.height(8.dp))
        when (trailer) {
            is TrailerState.Available -> {
                var cannotPlayInApp by rememberSaveable(trailer.video.key) { mutableStateOf(false) }
                if (cannotPlayInApp) {
                    InfoText(stringResource(R.string.trailer_cannot_play))
                } else {
                    key(trailer.video.key) {
                        YouTubeTrailerPlayer(
                            videoId = trailer.video.key,
                            onError = { cannotPlayInApp = true },
                            modifier = Modifier.clip(RoundedCornerShape(12.dp)),
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { runCatching { uriHandler.openUri(trailer.video.youtubeUrl) } },
                ) {
                    Icon(Icons.Filled.PlayArrow, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.action_watch_on_youtube))
                }
            }

            TrailerState.NotAvailable -> InfoText(stringResource(R.string.trailer_not_available))

            is TrailerState.Failed -> InfoText(stringResource(R.string.trailer_failed))
        }
    }
}

private fun LazyListScope.reviewItems(reviews: LazyPagingItems<Review>) {
    val refresh = reviews.loadState.refresh
    val isEmpty = reviews.itemCount == 0

    when {
        refresh is LoadState.Loading && isEmpty -> item(key = "reviews_loading") {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(modifier = Modifier.size(32.dp))
            }
        }

        refresh is LoadState.Error && isEmpty -> item(key = "reviews_error") {
            InlineError(error = refresh.error.asAppError(), onRetry = reviews::retry)
        }

        refresh is LoadState.NotLoading && reviews.loadState.append.endOfPaginationReached && isEmpty ->
            item(key = "reviews_empty") {
                InfoText(
                    text = stringResource(R.string.empty_reviews),
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }

        else -> {
            items(
                count = reviews.itemCount,
                key = reviews.itemKey { it.id },
                contentType = reviews.itemContentType { "review" },
            ) { index ->
                reviews[index]?.let { review ->
                    ReviewItem(
                        review = review,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    )
                }
            }
            item(key = "reviews_footer") {
                PagingAppendFooter(
                    loadState = reviews.loadState.append,
                    onRetry = reviews::retry,
                )
            }
        }
    }
}

@Composable
private fun InlineError(error: AppError, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(error.messageRes()),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
        )
        TextButton(onClick = onRetry) {
            Text(stringResource(R.string.action_retry))
        }
    }
}

@Composable
private fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = modifier,
    )
}

@Composable
private fun InfoText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier,
    )
}