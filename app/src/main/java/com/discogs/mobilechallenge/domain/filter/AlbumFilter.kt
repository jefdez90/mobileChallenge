package com.discogs.mobilechallenge.domain.filter

import com.discogs.mobilechallenge.domain.model.Album

data class AlbumFilter(
    val year: Int? = null,
    val genre: String? = null,
    val label: String? = null,
) {
    val isEmpty: Boolean get() = year == null && genre == null && label == null

    fun matches(album: Album): Boolean =
        (year == null || album.year == year) &&
            (genre == null || genre in album.genres) &&
            (label == null || label in album.labels)
}

fun List<Album>.applyFilter(filter: AlbumFilter): List<Album> {
    if (filter.isEmpty) return this
    return filter { album -> filter.matches(album) }
}
