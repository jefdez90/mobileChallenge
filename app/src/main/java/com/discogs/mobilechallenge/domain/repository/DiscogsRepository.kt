package com.discogs.mobilechallenge.domain.repository

import androidx.paging.PagingData
import com.discogs.mobilechallenge.domain.model.Artist
import kotlinx.coroutines.flow.Flow

interface DiscogsRepository {
    fun searchArtists(query: String): Flow<PagingData<Artist>>
}
