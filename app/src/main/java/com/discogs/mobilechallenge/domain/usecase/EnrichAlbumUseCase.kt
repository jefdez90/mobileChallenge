package com.discogs.mobilechallenge.domain.usecase

import com.discogs.mobilechallenge.domain.model.Album
import com.discogs.mobilechallenge.domain.repository.FilterOptionsRepository
import com.discogs.mobilechallenge.domain.repository.ReleaseRepository
import javax.inject.Inject

class EnrichAlbumUseCase @Inject constructor(
    private val releaseRepository: ReleaseRepository,
    private val filterOptionsRepository: FilterOptionsRepository,
) {
    suspend operator fun invoke(album: Album): Album {
        filterOptionsRepository.addYear(album.year.takeIf { it > 0 })

        val mainReleaseId = album.mainReleaseId
        if (mainReleaseId == null) {
            album.labels.forEach { filterOptionsRepository.addLabel(it) }
            return album
        }

        val detail = releaseRepository.getRelease(mainReleaseId)
        detail.genres.forEach { filterOptionsRepository.addGenre(it) }
        detail.labels.forEach { filterOptionsRepository.addLabel(it) }

        return album.copy(
            genres = detail.genres,
            labels = detail.labels,
            imageUrl = detail.imageUrl.ifEmpty { album.imageUrl },
        )
    }
}
