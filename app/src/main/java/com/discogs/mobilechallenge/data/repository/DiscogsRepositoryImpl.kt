package com.discogs.mobilechallenge.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.discogs.mobilechallenge.data.remote.api.DiscogsApi
import com.discogs.mobilechallenge.data.remote.paging.AlbumsPagingSource
import com.discogs.mobilechallenge.data.remote.paging.ArtistSearchPagingSource
import com.discogs.mobilechallenge.domain.model.Album
import com.discogs.mobilechallenge.domain.model.Artist
import com.discogs.mobilechallenge.domain.model.ArtistDetail
import com.discogs.mobilechallenge.domain.repository.DiscogsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DiscogsRepositoryImpl @Inject constructor(
    private val api: DiscogsApi,
) : DiscogsRepository {

    companion object {
        const val PAGE_SIZE = 30
    }

    override fun searchArtists(query: String): Flow<PagingData<Artist>> =
        Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { ArtistSearchPagingSource(api, query) },
        ).flow

    override suspend fun getArtistDetail(artistId: Int): ArtistDetail =
        api.getArtist(artistId).toArtistDetail()

    override fun getArtistAlbums(artistId: Int): Flow<PagingData<Album>> =
        Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { AlbumsPagingSource(api, artistId) },
        ).flow
}
