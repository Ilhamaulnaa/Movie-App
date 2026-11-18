package com.ilham.presentation.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import com.ilham.common.AppError
import com.ilham.presentation.R

object TestTags {
    const val LOADING = "loading"
    const val ERROR = "error"
    const val EMPTY = "empty"
    const val GENRE_GRID = "genre_grid"
    const val MOVIE_GRID = "movie_grid"
    const val DETAIL_LIST = "detail_list"
}

@Composable
fun LoadingState(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(modifier = Modifier.testTag(TestTags.LOADING))
    }
}

@Composable
fun ErrorState(error: AppError, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp)
            .testTag(TestTags.ERROR),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Filled.Warning,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(56.dp),
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(error.messageRes()),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(24.dp))
        Button(onClick = onRetry) {
            Icon(Icons.Filled.Refresh, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(stringResource(R.string.action_retry))
        }
    }
}

@Composable
fun EmptyState(message: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp)
            .testTag(TestTags.EMPTY),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Filled.Info,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(56.dp),
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun PagingAppendFooter(
    loadState: LoadState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    showEndMessage: Boolean = false,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        when (loadState) {
            is LoadState.Loading -> CircularProgressIndicator(modifier = Modifier.size(32.dp))

            is LoadState.Error -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(loadState.error.asAppError().messageRes()),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                )
                TextButton(onClick = onRetry) {
                    Text(stringResource(R.string.action_retry))
                }
            }

            is LoadState.NotLoading -> if (showEndMessage && loadState.endOfPaginationReached) {
                Text(
                    text = stringResource(R.string.end_of_list),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
fun BackButton(onBack: () -> Unit) {
    IconButton(onClick = onBack) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(R.string.action_back),
        )
    }
}