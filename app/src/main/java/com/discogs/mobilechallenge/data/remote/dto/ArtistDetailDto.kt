package com.discogs.mobilechallenge.data.remote.dto

import com.discogs.mobilechallenge.domain.model.ArtistDetail
import com.discogs.mobilechallenge.domain.model.BandMember
import com.google.gson.annotations.SerializedName

data class ArtistDetailDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("profile") val profile: String?,
    @SerializedName("images") val images: List<ArtistImageDto>?,
    @SerializedName("urls") val urls: List<String>?,
    @SerializedName("members") val members: List<BandMemberDto>?,
) {
    fun toArtistDetail() = ArtistDetail(
        id = id,
        name = name,
        profile = profile.orEmpty(),
        imageUrl = images?.firstOrNull { it.type == "primary" }?.uri
            ?: images?.firstOrNull()?.uri
            ?: "",
        urls = urls.orEmpty(),
        members = members?.map { it.toBandMember() }.orEmpty(),
    )
}

data class ArtistImageDto(
    @SerializedName("type") val type: String,
    @SerializedName("uri") val uri: String,
)

data class BandMemberDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("active") val active: Boolean?,
    @SerializedName("thumbnail_url") val thumbnailUrl: String?,
) {
    fun toBandMember() = BandMember(
        id = id,
        name = name,
        active = active ?: false,
        thumbnailUrl = thumbnailUrl.orEmpty(),
    )
}
