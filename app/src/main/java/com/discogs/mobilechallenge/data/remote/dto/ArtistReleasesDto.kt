package com.discogs.mobilechallenge.data.remote.dto

import com.discogs.mobilechallenge.domain.model.Album
import com.google.gson.annotations.SerializedName

data class ArtistReleasesDto(
    @SerializedName("pagination") val pagination: PaginationDto,
    @SerializedName("releases") val releases: List<ArtistReleaseItemDto>,
)

data class ArtistReleaseItemDto(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("year") val year: Int?,
    @SerializedName("type") val type: String,
    @SerializedName("role") val role: String,
    @SerializedName("label") val label: String?,
    @SerializedName("thumb") val thumb: String?,
    @SerializedName("main_release") val mainRelease: Int?,
    @SerializedName("resource_url") val resourceUrl: String,
) {
    // Domain rule: only main master releases represent canonical album entries.
    // Reissues, singles, compilations, and guest appearances are excluded.
    fun toAlbumOrNull(): Album? {
        if (type != "master" || role != "Main") return null
        return Album(
            id = id,
            title = title,
            year = year ?: 0,
            genres = emptyList(),
            labels = listOfNotNull(label?.takeIf { it.isNotBlank() }),
            imageUrl = thumb.orEmpty(),
        )
    }
}
