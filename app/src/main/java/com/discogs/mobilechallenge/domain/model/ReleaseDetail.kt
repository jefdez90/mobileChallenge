package com.discogs.mobilechallenge.domain.model

data class ReleaseDetail(
    val id: Int,
    val genres: List<String>,
    val labels: List<String>,
    val imageUrl: String,
)
