package com.discogs.mobilechallenge.presentation.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.discogs.mobilechallenge.domain.model.Artist
import com.discogs.mobilechallenge.ui.theme.MobileChallengeTheme
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun SearchScreen(
    onArtistClick: (Int) -> Unit,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val query by viewModel.query.collectAsState()
    val lazyItems = viewModel.artists.collectAsLazyPagingItems()
    SearchContent(
        query = query,
        lazyItems = lazyItems,
        onQueryChange = viewModel::onQueryChange,
        onArtistClick = onArtistClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchContent(
    query: String,
    lazyItems: LazyPagingItems<Artist>,
    onQueryChange: (String) -> Unit,
    onArtistClick: (Int) -> Unit,
) {
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = { TopAppBar(title = { Text("Discogs Explorer") }) },
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                label = { Text("Search artists") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            )

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    query.isBlank() -> EmptyPrompt()

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

                    lazyItems.itemCount == 0 -> {
                        Text(
                            text = "No results for \"$query\"",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.align(Alignment.Center),
                        )
                    }

                    else -> {
                        LazyColumn {
                            items(count = lazyItems.itemCount) { index ->
                                val artist = lazyItems[index]
                                if (artist != null) {
                                    ArtistRow(
                                        artist = artist,
                                        onClick = { onArtistClick(artist.id) },
                                    )
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
}

@Preview(showBackground = true, name = "Empty state")
@Composable
private fun SearchContentEmptyPreview() {
    MobileChallengeTheme {
        SearchContent(
            query = "",
            lazyItems = MutableStateFlow(PagingData.empty<Artist>()).collectAsLazyPagingItems(),
            onQueryChange = {},
            onArtistClick = {},
        )
    }
}

@Preview(showBackground = true, name = "Results")
@Composable
private fun SearchContentResultsPreview() {
    MobileChallengeTheme {
        SearchContent(
            query = "cafe",
            lazyItems = MutableStateFlow(
                PagingData.from(
                    listOf(
                        Artist(id = 1, name = "Cafe Tacuba", thumb = ""),
                        Artist(id = 2, name = "Cafe del Mar", thumb = ""),
                        Artist(id = 3, name = "Cafe de Paris", thumb = ""),
                    )
                )
            ).collectAsLazyPagingItems(),
            onQueryChange = {},
            onArtistClick = {},
        )
    }
}

@Preview(showBackground = true, name = "No results")
@Composable
private fun SearchContentNoResultsPreview() {
    MobileChallengeTheme {
        SearchContent(
            query = "xyz",
            lazyItems = MutableStateFlow(PagingData.from(emptyList<Artist>())).collectAsLazyPagingItems(),
            onQueryChange = {},
            onArtistClick = {},
        )
    }
}

@Composable
private fun EmptyPrompt() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "Search for an artist to get started",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
