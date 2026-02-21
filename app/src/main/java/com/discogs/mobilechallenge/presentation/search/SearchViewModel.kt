package com.discogs.mobilechallenge.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.discogs.mobilechallenge.domain.model.Artist
import com.discogs.mobilechallenge.domain.repository.DiscogsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: DiscogsRepository,
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    val artists: Flow<PagingData<Artist>> = _query
        .debounce(400)
        .flatMapLatest { q ->
            if (q.isBlank()) flowOf(PagingData.empty())
            else repository.searchArtists(q)
        }
        .cachedIn(viewModelScope)

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
    }
}
