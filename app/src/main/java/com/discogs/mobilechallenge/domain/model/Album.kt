package com.discogs.mobilechallenge.domain.model

data class Album(
    val id: Int,
    val title: String,
    val year: Int,
    val genres: List<String>,
    val labels: List<String>,
    val imageUrl: String,
)
