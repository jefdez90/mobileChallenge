package com.discogs.mobilechallenge.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.discogs.mobilechallenge.data.remote.api.DiscogsApi
import com.discogs.mobilechallenge.domain.model.Artist

class ArtistSearchPagingSource(
    private val api: DiscogsApi,
    private val query: String,
) : PagingSource<Int, Artist>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Artist> {
        val page = params.key ?: 1
        return try {
            val response = api.searchArtists(query = query, page = page)
            val artists = response.results.map { it.toArtist() }
            LoadResult.Page(
                data = artists,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (page >= response.pagination.pages) null else page + 1,
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Artist>): Int? {
        val anchor = state.anchorPosition ?: return null
        val closest = state.closestPageToPosition(anchor)
        return closest?.prevKey?.plus(1) ?: closest?.nextKey?.minus(1)
    }
}
