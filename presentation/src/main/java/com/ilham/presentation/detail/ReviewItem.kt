package com.ilham.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.ilham.domain.model.Review
import com.ilham.presentation.R
import com.ilham.presentation.common.Formatters
import com.ilham.presentation.common.RatingBadge

private const val COLLAPSED_MAX_LINES = 5

@Composable
fun ReviewItem(review: Review, modifier: Modifier = Modifier) {

    var expanded by rememberSaveable(review.id) { mutableStateOf(false) }
    var isOverflowing by rememberSaveable(review.id) { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val avatarFallback = rememberVectorPainter(Icons.Filled.Person)
                AsyncImage(
                    model = review.avatarUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    placeholder = avatarFallback,
                    error = avatarFallback,
                    fallback = avatarFallback,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = review.author,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Formatters.reviewDate(review.createdAt)?.let { date ->
                        Text(
                            text = date,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                review.rating?.let { RatingBadge(rating = it) }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = review.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = if (expanded) Int.MAX_VALUE else COLLAPSED_MAX_LINES,
                overflow = TextOverflow.Ellipsis,
                onTextLayout = { result ->
                    if (!expanded) isOverflowing = result.hasVisualOverflow
                },
            )

            if (isOverflowing || expanded) {
                TextButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.align(Alignment.End),
                ) {
                    Text(
                        stringResource(if (expanded) R.string.action_show_less else R.string.action_read_more),
                    )
                }
            }
        }
    }
}