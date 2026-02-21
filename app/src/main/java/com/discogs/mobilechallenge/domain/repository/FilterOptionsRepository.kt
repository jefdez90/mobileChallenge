package com.discogs.mobilechallenge.domain.repository

import com.discogs.mobilechallenge.domain.model.FilterOptions
import kotlinx.coroutines.flow.StateFlow

interface FilterOptionsRepository {
    val options: StateFlow<FilterOptions>
    fun reset()
    fun addYear(year: Int?)
    fun addLabel(label: String?)
    fun addGenre(genre: String?)
}
