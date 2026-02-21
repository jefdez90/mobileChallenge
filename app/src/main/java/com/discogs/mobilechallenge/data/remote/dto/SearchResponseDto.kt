package com.discogs.mobilechallenge.data.remote.dto

import com.discogs.mobilechallenge.domain.model.Artist
import com.google.gson.annotations.SerializedName

data class SearchResponseDto(
    @SerializedName("pagination") val pagination: PaginationDto,
    @SerializedName("results") val results: List<SearchResultDto>,
)

data class PaginationDto(
    @SerializedName("page") val page: Int,
    @SerializedName("pages") val pages: Int,
    @SerializedName("per_page") val perPage: Int,
    @SerializedName("items") val items: Int,
)

data class SearchResultDto(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("thumb") val thumb: String,
) {
    fun toArtist() = Artist(id = id, name = title, thumb = thumb)
}
