package com.discogs.mobilechallenge.presentation.albums

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.discogs.mobilechallenge.domain.filter.AlbumFilter
import com.discogs.mobilechallenge.domain.model.Album
import com.discogs.mobilechallenge.domain.model.FilterOptions
import com.discogs.mobilechallenge.domain.repository.DiscogsRepository
import com.discogs.mobilechallenge.domain.repository.FilterOptionsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AlbumsViewModel @Inject constructor(
    private val repository: DiscogsRepository,
    private val filterOptionsRepository: FilterOptionsRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val artistId: Int = checkNotNull(savedStateHandle["artistId"])

    val filterOptions: StateFlow<FilterOptions> = filterOptionsRepository.options

    private val _filter = MutableStateFlow(AlbumFilter())
    val filter: StateFlow<AlbumFilter> = _filter.asStateFlow()

    // Cached once: re-subscriptions share the same Pager without triggering new network requests
    private val pagedAlbums: Flow<PagingData<Album>> = repository
        .getArtistAlbums(artistId)
        .cachedIn(viewModelScope)

    // flatMapLatest re-applies the filter transformation over cached data on every filter change
    val albums: Flow<PagingData<Album>> = _filter
        .flatMapLatest { currentFilter ->
            pagedAlbums.map { pagingData ->
                if (currentFilter.isEmpty) pagingData
                else pagingData.filter { currentFilter.matches(it) }
            }
        }

    init {
        filterOptionsRepository.reset()
    }

    fun setFilter(filter: AlbumFilter) {
        _filter.value = filter
    }

    fun clearFilter() {
        _filter.value = AlbumFilter()
    }
}
