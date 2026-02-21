package com.discogs.mobilechallenge.domain.repository

import androidx.paging.PagingData
import com.discogs.mobilechallenge.domain.model.Artist
import com.discogs.mobilechallenge.domain.model.ArtistDetail
import kotlinx.coroutines.flow.Flow

interface DiscogsRepository {
    fun searchArtists(query: String): Flow<PagingData<Artist>>
    suspend fun getArtistDetail(artistId: Int): ArtistDetail
}
