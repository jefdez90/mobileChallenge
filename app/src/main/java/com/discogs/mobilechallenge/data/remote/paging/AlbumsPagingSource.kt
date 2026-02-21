package com.discogs.mobilechallenge.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.discogs.mobilechallenge.domain.repository.FilterOptionsRepository
import com.discogs.mobilechallenge.data.remote.api.DiscogsApi
import com.discogs.mobilechallenge.domain.model.Album

class AlbumsPagingSource(
    private val api: DiscogsApi,
    private val artistId: Int,
    private val filterOptionsRepository: FilterOptionsRepository,
) : PagingSource<Int, Album>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Album> {
        val page = params.key ?: 1
        return try {
            val response = api.getArtistReleases(artistId = artistId, page = page)
            val albums = response.releases
                .filter { it.type == "master" && it.role == "Main" } // only albums
                .map { release ->
                    filterOptionsRepository.addYear(release.year)
                    filterOptionsRepository.addLabel(release.label)
                    Album(
                        id = release.id,
                        title = release.title,
                        year = release.year ?: 0,
                        genres = emptyList(),
                        labels = listOfNotNull(release.label?.takeIf { it.isNotBlank() }),
                        imageUrl = release.thumb.orEmpty(),
                    )
                }
            LoadResult.Page(
                data = albums,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (page >= response.pagination.pages) null else page + 1,
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Album>): Int? {
        val anchor = state.anchorPosition ?: return null
        val closest = state.closestPageToPosition(anchor)
        return closest?.prevKey?.plus(1) ?: closest?.nextKey?.minus(1)
    }
}
