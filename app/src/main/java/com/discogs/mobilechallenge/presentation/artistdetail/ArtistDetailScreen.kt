package com.discogs.mobilechallenge.presentation.artistdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.discogs.mobilechallenge.domain.model.ArtistDetail
import com.discogs.mobilechallenge.domain.model.BandMember
import com.discogs.mobilechallenge.presentation.common.UiState
import com.discogs.mobilechallenge.presentation.search.ErrorMessage
import com.discogs.mobilechallenge.ui.theme.MobileChallengeTheme

@Composable
fun ArtistDetailScreen(
    onAlbumsClick: (Int) -> Unit,
    viewModel: ArtistDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    ArtistDetailContent(
        uiState = uiState,
        onAlbumsClick = onAlbumsClick,
        onRetry = viewModel::retry,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistDetailContent(
    uiState: UiState<ArtistDetail>,
    onAlbumsClick: (Int) -> Unit,
    onRetry: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val title = (uiState as? UiState.Success)?.data?.name ?: "Artist"
                    Text(title)
                },
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            when (uiState) {
                is UiState.Loading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                )

                is UiState.Error -> ErrorMessage(
                    message = uiState.message,
                    onRetry = onRetry,
                    modifier = Modifier.align(Alignment.Center),
                )

                is UiState.Success -> ArtistDetailBody(
                    artist = uiState.data,
                    onAlbumsClick = onAlbumsClick,
                )
            }
        }
    }
}

@Composable
private fun ArtistDetailBody(
    artist: ArtistDetail,
    onAlbumsClick: (Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (artist.imageUrl.isNotBlank()) {
            AsyncImage(
                model = artist.imageUrl,
                contentDescription = artist.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
            )
        }

        Text(
            text = artist.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )

        if (artist.profile.isNotBlank()) {
            Text(
                text = artist.profile,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        if (artist.members.isNotEmpty()) {
            MembersSection(members = artist.members)
        }

        Button(
            onClick = { onAlbumsClick(artist.id) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("View Albums")
        }
    }
}

// region Previews

private val previewArtist = ArtistDetail(
    id = 79144,
    name = "Cafe Tacuba",
    profile = "Mexican indie rock and latin rock band from Naucalpan, Mexico, formed in 1989.",
    imageUrl = "",
    urls = emptyList(),
    members = listOf(
        BandMember(id = 1, name = "Quique Rangel", active = true, thumbnailUrl = ""),
        BandMember(id = 2, name = "Rubén Albarrán", active = true, thumbnailUrl = ""),
        BandMember(id = 3, name = "Emmanuel del Real", active = false, thumbnailUrl = ""),
    ),
)

@Preview(showBackground = true, name = "Loading")
@Composable
private fun ArtistDetailLoadingPreview() {
    MobileChallengeTheme {
        ArtistDetailContent(
            uiState = UiState.Loading,
            onAlbumsClick = {},
            onRetry = {},
        )
    }
}

@Preview(showBackground = true, name = "Success — band with members")
@Composable
private fun ArtistDetailSuccessPreview() {
    MobileChallengeTheme {
        ArtistDetailContent(
            uiState = UiState.Success(previewArtist),
            onAlbumsClick = {},
            onRetry = {},
        )
    }
}

@Preview(showBackground = true, name = "Success — solo artist")
@Composable
private fun ArtistDetailSoloPreview() {
    MobileChallengeTheme {
        ArtistDetailContent(
            uiState = UiState.Success(previewArtist.copy(members = emptyList())),
            onAlbumsClick = {},
            onRetry = {},
        )
    }
}

@Preview(showBackground = true, name = "Error")
@Composable
private fun ArtistDetailErrorPreview() {
    MobileChallengeTheme {
        ArtistDetailContent(
            uiState = UiState.Error("Failed to load artist. Check your connection."),
            onAlbumsClick = {},
            onRetry = {},
        )
    }
}

// endregion
