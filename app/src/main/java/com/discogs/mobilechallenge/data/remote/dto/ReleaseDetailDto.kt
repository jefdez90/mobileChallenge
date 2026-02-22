package com.discogs.mobilechallenge.data.remote.dto

import com.discogs.mobilechallenge.domain.model.ReleaseDetail
import com.google.gson.annotations.SerializedName

data class ReleaseDetailDto(
    @SerializedName("id") val id: Int,
    @SerializedName("genres") val genres: List<String>?,
    @SerializedName("labels") val labels: List<ReleaseLabelDto>?,
    @SerializedName("images") val images: List<ReleaseImageDto>?,
) {
    fun toReleaseDetail() = ReleaseDetail(
        id = id,
        genres = genres.orEmpty(),
        labels = labels.orEmpty().map { it.name }.filter { it.isNotBlank() },
        imageUrl = images?.firstOrNull { it.type == "primary" }?.uri150.orEmpty()
            .ifEmpty { images?.firstOrNull()?.uri150.orEmpty() },
    )
}

data class ReleaseLabelDto(@SerializedName("name") val name: String)

data class ReleaseImageDto(
    @SerializedName("type") val type: String,
    @SerializedName("uri") val uri: String,
    @SerializedName("uri150") val uri150: String?,
)
