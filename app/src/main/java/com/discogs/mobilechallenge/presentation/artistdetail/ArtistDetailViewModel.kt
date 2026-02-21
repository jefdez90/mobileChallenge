package com.discogs.mobilechallenge.presentation.artistdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.discogs.mobilechallenge.domain.model.ArtistDetail
import com.discogs.mobilechallenge.domain.repository.DiscogsRepository
import com.discogs.mobilechallenge.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistDetailViewModel @Inject constructor(
    private val repository: DiscogsRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val artistId: Int = checkNotNull(savedStateHandle["artistId"])

    private val _uiState = MutableStateFlow<UiState<ArtistDetail>>(UiState.Loading)
    val uiState: StateFlow<UiState<ArtistDetail>> = _uiState.asStateFlow()

    init {
        loadArtistDetail()
    }

    fun retry() {
        loadArtistDetail()
    }

    private fun loadArtistDetail() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                _uiState.value = UiState.Success(repository.getArtistDetail(artistId))
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "Something went wrong")
            }
        }
    }
}
