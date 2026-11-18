package com.ilham.presentation.movies

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.ilham.domain.model.Movie
import com.ilham.presentation.R
import com.ilham.presentation.common.Formatters
import com.ilham.presentation.common.RatingBadge

@Composable
fun MovieCard(movie: Movie, onClick: () -> Unit, modifier: Modifier = Modifier) {

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
    ) {
        Box {
            val placeholder = painterResource(R.drawable.ic_movie_placeholder)
            AsyncImage(
                model = movie.posterUrl,
                contentDescription = movie.title,
                contentScale = ContentScale.Crop,
                placeholder = placeholder,
                error = placeholder,
                fallback = placeholder,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f / 3f)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
            )
            RatingBadge(
                rating = movie.rating,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp),
            )
        }
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
            Text(
                text = movie.title,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 2,
                minLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = Formatters.releaseYear(movie.releaseDate) ?: stringResource(R.string.unknown_value),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
