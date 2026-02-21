package com.discogs.mobilechallenge.presentation.albums

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.discogs.mobilechallenge.domain.model.Album
import com.discogs.mobilechallenge.presentation.search.ErrorMessage
import com.discogs.mobilechallenge.ui.theme.MobileChallengeTheme
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun AlbumsScreen(
    viewModel: AlbumsViewModel = hiltViewModel(),
) {
    val lazyItems = viewModel.albums.collectAsLazyPagingItems()
    AlbumsContent(lazyItems = lazyItems)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumsContent(
    lazyItems: LazyPagingItems<Album>,
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Albums") }) },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            when {
                lazyItems.loadState.refresh is LoadState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                lazyItems.loadState.refresh is LoadState.Error -> {
                    val error = (lazyItems.loadState.refresh as LoadState.Error).error
                    ErrorMessage(
                        message = error.localizedMessage ?: "Something went wrong",
                        onRetry = { lazyItems.retry() },
                        modifier = Modifier.align(Alignment.Center),
                    )
                }

                lazyItems.itemCount == 0 && lazyItems.loadState.refresh is LoadState.NotLoading -> {
                    Text(
                        text = "No albums found",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }

                else -> {
                    LazyColumn {
                        items(count = lazyItems.itemCount) { index ->
                            val album = lazyItems[index]
                            if (album != null) {
                                AlbumRow(album = album)
                                HorizontalDivider()
                            }
                        }

                        when (val append = lazyItems.loadState.append) {
                            is LoadState.Loading -> item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    CircularProgressIndicator()
                                }
                            }

                            is LoadState.Error -> item {
                                ErrorMessage(
                                    message = append.error.localizedMessage ?: "Load error",
                                    onRetry = { lazyItems.retry() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                )
                            }

                            else -> Unit
                        }
                    }
                }
            }
        }
    }
}

// region Previews

private val previewAlbums = listOf(
    Album(id = 1, title = "Cuatro Caminos", year = 2003, genres = listOf("Rock"), labels = listOf("Universal"), imageUrl = ""),
    Album(id = 2, title = "Re", year = 1994, genres = listOf("Rock", "Latin"), labels = listOf("WEA"), imageUrl = ""),
    Album(id = 3, title = "Café Tacuba", year = 1992, genres = listOf("Rock"), labels = listOf("WEA"), imageUrl = ""),
)

@Preview(showBackground = true, name = "Results")
@Composable
private fun AlbumsContentResultsPreview() {
    MobileChallengeTheme {
        AlbumsContent(
            lazyItems = MutableStateFlow(PagingData.from(previewAlbums)).collectAsLazyPagingItems(),
        )
    }
}

@Preview(showBackground = true, name = "Empty")
@Composable
private fun AlbumsContentEmptyPreview() {
    MobileChallengeTheme {
        AlbumsContent(
            lazyItems = MutableStateFlow(PagingData.from(emptyList<Album>())).collectAsLazyPagingItems(),
        )
    }
}

// endregion
