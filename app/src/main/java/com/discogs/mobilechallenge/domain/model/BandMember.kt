package com.discogs.mobilechallenge.domain.model

data class BandMember(
    val id: Int,
    val name: String,
    val active: Boolean,
    val thumbnailUrl: String,
)
