package com.discogs.mobilechallenge.data.remote.dto

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
)
