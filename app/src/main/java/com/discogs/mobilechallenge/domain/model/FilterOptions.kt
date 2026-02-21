package com.discogs.mobilechallenge.domain.model

data class FilterOptions(
    val years: Set<Int> = emptySet(),
    val genres: Set<String> = emptySet(),
    val labels: Set<String> = emptySet(),
)