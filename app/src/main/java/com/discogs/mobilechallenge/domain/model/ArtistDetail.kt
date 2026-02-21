package com.discogs.mobilechallenge.domain.model

data class ArtistDetail(
    val id: Int,
    val name: String,
    val profile: String,
    val imageUrl: String,
    val urls: List<String>,
    val members: List<BandMember>,
)
