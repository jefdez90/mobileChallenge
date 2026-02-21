package com.discogs.mobilechallenge.presentation.albums

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.discogs.mobilechallenge.domain.model.Album
import com.discogs.mobilechallenge.domain.repository.DiscogsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class AlbumsViewModel @Inject constructor(
    private val repository: DiscogsRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val artistId: Int = checkNotNull(savedStateHandle["artistId"])

    val albums: Flow<PagingData<Album>> = repository
        .getArtistAlbums(artistId)
        .cachedIn(viewModelScope)
}
