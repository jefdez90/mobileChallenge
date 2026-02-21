package com.discogs.mobilechallenge.presentation.albums

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.discogs.mobilechallenge.domain.model.Album
import com.discogs.mobilechallenge.ui.theme.MobileChallengeTheme

@Composable
fun AlbumRow(album: Album) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = album.title.firstOrNull()?.uppercase() ?: "",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            AsyncImage(
                model = album.imageUrl.ifEmpty { null },
                contentDescription = album.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = album.title,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (album.year > 0) {
                Text(
                    text = album.year.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AlbumRowPreview() {
    MobileChallengeTheme {
        AlbumRow(
            album = Album(
                id = 1,
                title = "Cuatro Caminos",
                year = 2003,
                genres = listOf("Rock", "Latin"),
                labels = listOf("Universal"),
                imageUrl = "",
            ),
        )
    }
}

@Preview(showBackground = true, name = "No year")
@Composable
private fun AlbumRowNoYearPreview() {
    MobileChallengeTheme {
        AlbumRow(
            album = Album(
                id = 2,
                title = "Re",
                year = 0,
                genres = emptyList(),
                labels = emptyList(),
                imageUrl = "",
            ),
        )
    }
}
