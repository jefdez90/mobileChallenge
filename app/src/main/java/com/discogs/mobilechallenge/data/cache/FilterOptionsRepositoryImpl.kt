package com.discogs.mobilechallenge.data.cache

import com.discogs.mobilechallenge.domain.model.FilterOptions
import com.discogs.mobilechallenge.domain.repository.FilterOptionsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FilterOptionsRepositoryImpl @Inject constructor() : FilterOptionsRepository {

    private val _options = MutableStateFlow(FilterOptions())
    override val options: StateFlow<FilterOptions> = _options.asStateFlow()

    override fun reset() {
        _options.value = FilterOptions()
    }

    override fun addYear(year: Int?) {
        if (year != null && year > 0) _options.update { it.copy(years = it.years + year) }
    }

    override fun addLabel(label: String?) {
        if (!label.isNullOrBlank()) _options.update { it.copy(labels = it.labels + label) }
    }

    override fun addGenre(genre: String?) {
        if (!genre.isNullOrBlank()) _options.update { it.copy(genres = it.genres + genre) }
    }
}
