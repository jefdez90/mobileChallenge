package com.discogs.mobilechallenge.presentation.albums

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.discogs.mobilechallenge.domain.filter.AlbumFilter
import com.discogs.mobilechallenge.domain.model.Album
import com.discogs.mobilechallenge.domain.model.FilterOptions
import com.discogs.mobilechallenge.presentation.search.ErrorMessage
import com.discogs.mobilechallenge.ui.theme.MobileChallengeTheme
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun AlbumsScreen(
    viewModel: AlbumsViewModel = hiltViewModel(),
) {
    val lazyItems = viewModel.albums.collectAsLazyPagingItems()
    val filterOptions by viewModel.filterOptions.collectAsState()
    val filter by viewModel.filter.collectAsState()

    AlbumsContent(
        lazyItems = lazyItems,
        filterOptions = filterOptions,
        filter = filter,
        onFilterChange = viewModel::setFilter,
        onClearFilter = viewModel::clearFilter,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumsContent(
    lazyItems: LazyPagingItems<Album>,
    filterOptions: FilterOptions,
    filter: AlbumFilter,
    onFilterChange: (AlbumFilter) -> Unit,
    onClearFilter: () -> Unit,
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Albums") }) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            if (filterOptions.years.isNotEmpty() || filterOptions.labels.isNotEmpty() || filterOptions.genres.isNotEmpty()) {
                FilterPanel(
                    filterOptions = filterOptions,
                    filter = filter,
                    onFilterChange = onFilterChange,
                    onClearFilter = onClearFilter,
                )
                HorizontalDivider()
            }

            when {
                lazyItems.loadState.refresh is LoadState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                lazyItems.loadState.refresh is LoadState.Error -> {
                    val error = (lazyItems.loadState.refresh as LoadState.Error).error
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        ErrorMessage(
                            message = error.localizedMessage ?: "Something went wrong",
                            onRetry = { lazyItems.retry() },
                        )
                    }
                }

                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        if (lazyItems.itemCount == 0 && lazyItems.loadState.refresh is LoadState.NotLoading) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = "No albums found",
                                        style = MaterialTheme.typography.bodyLarge,
                                    )
                                }
                            }
                        }

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

@Composable
private fun FilterPanel(
    filterOptions: FilterOptions,
    filter: AlbumFilter,
    onFilterChange: (AlbumFilter) -> Unit,
    onClearFilter: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        if (filterOptions.years.isNotEmpty()) {
            ChipRow(label = "Year") {
                filterOptions.years.sorted().forEach { year ->
                    FilterChip(
                        selected = filter.year == year,
                        onClick = {
                            onFilterChange(filter.copy(year = if (filter.year == year) null else year))
                        },
                        label = { Text(year.toString()) },
                    )
                }
            }
        }

        if (filterOptions.labels.isNotEmpty()) {
            ChipRow(label = "Label") {
                filterOptions.labels.sorted().forEach { label ->
                    FilterChip(
                        selected = filter.label == label,
                        onClick = {
                            onFilterChange(filter.copy(label = if (filter.label == label) null else label))
                        },
                        label = { Text(label) },
                    )
                }
            }
        }

        if (filterOptions.genres.isNotEmpty()) {
            ChipRow(label = "Genre") {
                filterOptions.genres.sorted().forEach { genre ->
                    FilterChip(
                        selected = filter.genre == genre,
                        onClick = {
                            onFilterChange(filter.copy(genre = if (filter.genre == genre) null else genre))
                        },
                        label = { Text(genre) },
                    )
                }
            }
        }

        if (!filter.isEmpty) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                TextButton(onClick = onClearFilter) {
                    Text("Clear all")
                }
            }
        }
    }
}

@Composable
private fun ChipRow(
    label: String,
    chips: @Composable () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(end = 4.dp),
        )
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            chips()
        }
    }
}

// region Previews

private val previewAlbums = listOf(
    Album(id = 1, title = "Cuatro Caminos", year = 2003, genres = listOf("Rock"), labels = listOf("Universal"), imageUrl = ""),
    Album(id = 2, title = "Re", year = 1994, genres = listOf("Rock", "Latin"), labels = listOf("WEA"), imageUrl = ""),
    Album(id = 3, title = "Café Tacuba", year = 1992, genres = listOf("Rock"), labels = listOf("WEA"), imageUrl = ""),
)

private val previewFilterOptions = FilterOptions(
    years = setOf(1992, 1994, 2003),
    genres = setOf("Rock", "Latin"),
    labels = setOf("Universal", "WEA"),
)

@Preview(showBackground = true, name = "Results with filters")
@Composable
private fun AlbumsContentResultsPreview() {
    MobileChallengeTheme {
        AlbumsContent(
            lazyItems = MutableStateFlow(PagingData.from(previewAlbums)).collectAsLazyPagingItems(),
            filterOptions = previewFilterOptions,
            filter = AlbumFilter(year = 1994),
            onFilterChange = {},
            onClearFilter = {},
        )
    }
}

@Preview(showBackground = true, name = "No filters available")
@Composable
private fun AlbumsContentNoFiltersPreview() {
    MobileChallengeTheme {
        AlbumsContent(
            lazyItems = MutableStateFlow(PagingData.from(previewAlbums)).collectAsLazyPagingItems(),
            filterOptions = FilterOptions(),
            filter = AlbumFilter(),
            onFilterChange = {},
            onClearFilter = {},
        )
    }
}

@Preview(showBackground = true, name = "Empty")
@Composable
private fun AlbumsContentEmptyPreview() {
    MobileChallengeTheme {
        AlbumsContent(
            lazyItems = MutableStateFlow(PagingData.from(emptyList<Album>())).collectAsLazyPagingItems(),
            filterOptions = FilterOptions(),
            filter = AlbumFilter(),
            onFilterChange = {},
            onClearFilter = {},
        )
    }
}

// endregion
