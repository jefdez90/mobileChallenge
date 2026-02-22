package com.discogs.mobilechallenge.domain.usecase

import com.discogs.mobilechallenge.data.cache.FilterOptionsRepositoryImpl
import com.discogs.mobilechallenge.domain.model.Album
import com.discogs.mobilechallenge.domain.model.ReleaseDetail
import com.discogs.mobilechallenge.domain.repository.ReleaseRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class EnrichAlbumUseCaseTest {

    private lateinit var filterOptionsRepository: FilterOptionsRepositoryImpl
    private lateinit var useCase: EnrichAlbumUseCase

    @Before
    fun setUp() {
        filterOptionsRepository = FilterOptionsRepositoryImpl()
    }

    private fun fakeReleaseRepository(detail: ReleaseDetail): ReleaseRepository =
        object : ReleaseRepository {
            override suspend fun getRelease(releaseId: Int): ReleaseDetail = detail
        }

    private fun baseAlbum(
        id: Int = 1,
        title: String = "Re",
        year: Int = 1994,
        labels: List<String> = listOf("WEA"),
        imageUrl: String = "https://thumb.example.com/re.jpg",
        mainReleaseId: Int? = 100,
    ) = Album(
        id = id,
        title = title,
        year = year,
        genres = emptyList(),
        labels = labels,
        imageUrl = imageUrl,
        mainReleaseId = mainReleaseId,
    )

    // region enrichment of genres, labels, imageUrl

    @Test
    fun `enriches genres and labels from release detail`() = runTest {
        val detail = ReleaseDetail(id = 100, genres = listOf("Rock", "Latin"), labels = listOf("WEA"), imageUrl = "")
        useCase = EnrichAlbumUseCase(fakeReleaseRepository(detail), filterOptionsRepository)

        val result = useCase(baseAlbum())

        assertEquals(listOf("Rock", "Latin"), result.genres)
        assertEquals(listOf("WEA"), result.labels)
    }

    @Test
    fun `enriches imageUrl from release detail when detail has image`() = runTest {
        val detailImageUrl = "https://detail.example.com/image.jpg"
        val detail = ReleaseDetail(id = 100, genres = listOf("Rock"), labels = listOf("WEA"), imageUrl = detailImageUrl)
        useCase = EnrichAlbumUseCase(fakeReleaseRepository(detail), filterOptionsRepository)

        val result = useCase(baseAlbum(imageUrl = "https://thumb.example.com/thumb.jpg"))

        assertEquals(detailImageUrl, result.imageUrl)
    }

    @Test
    fun `falls back to album imageUrl when detail imageUrl is empty`() = runTest {
        val albumImageUrl = "https://thumb.example.com/thumb.jpg"
        val detail = ReleaseDetail(id = 100, genres = listOf("Rock"), labels = listOf("WEA"), imageUrl = "")
        useCase = EnrichAlbumUseCase(fakeReleaseRepository(detail), filterOptionsRepository)

        val result = useCase(baseAlbum(imageUrl = albumImageUrl))

        assertEquals(albumImageUrl, result.imageUrl)
    }

    @Test
    fun `preserves id, title, and year after enrichment`() = runTest {
        val detail = ReleaseDetail(id = 100, genres = listOf("Rock"), labels = listOf("WEA"), imageUrl = "")
        useCase = EnrichAlbumUseCase(fakeReleaseRepository(detail), filterOptionsRepository)
        val album = baseAlbum(id = 42, title = "Cuatro Caminos", year = 2003)

        val result = useCase(album)

        assertEquals(42, result.id)
        assertEquals("Cuatro Caminos", result.title)
        assertEquals(2003, result.year)
    }

    // endregion

    // region FilterOptions side-effects

    @Test
    fun `registers year in FilterOptionsRepository`() = runTest {
        val detail = ReleaseDetail(id = 100, genres = listOf("Rock"), labels = listOf("WEA"), imageUrl = "")
        useCase = EnrichAlbumUseCase(fakeReleaseRepository(detail), filterOptionsRepository)

        useCase(baseAlbum(year = 1994))

        assertTrue(1994 in filterOptionsRepository.options.value.years)
    }

    @Test
    fun `registers genres in FilterOptionsRepository`() = runTest {
        val detail = ReleaseDetail(id = 100, genres = listOf("Rock", "Latin"), labels = listOf("WEA"), imageUrl = "")
        useCase = EnrichAlbumUseCase(fakeReleaseRepository(detail), filterOptionsRepository)

        useCase(baseAlbum())

        val genres = filterOptionsRepository.options.value.genres
        assertTrue("Rock" in genres)
        assertTrue("Latin" in genres)
    }

    @Test
    fun `registers labels in FilterOptionsRepository`() = runTest {
        val detail = ReleaseDetail(id = 100, genres = listOf("Rock"), labels = listOf("Universal", "MCA"), imageUrl = "")
        useCase = EnrichAlbumUseCase(fakeReleaseRepository(detail), filterOptionsRepository)

        useCase(baseAlbum())

        val labels = filterOptionsRepository.options.value.labels
        assertTrue("Universal" in labels)
        assertTrue("MCA" in labels)
    }

    // endregion

    // region mainReleaseId == null path

    @Test
    fun `returns album unchanged when mainReleaseId is null`() = runTest {
        val noOpReleaseRepository = object : ReleaseRepository {
            override suspend fun getRelease(releaseId: Int): ReleaseDetail =
                error("should not be called")
        }
        useCase = EnrichAlbumUseCase(noOpReleaseRepository, filterOptionsRepository)
        val album = baseAlbum(labels = listOf("Indie"), mainReleaseId = null)

        val result = useCase(album)

        assertEquals(album, result)
    }

    @Test
    fun `registers year and existing labels when mainReleaseId is null`() = runTest {
        val noOpReleaseRepository = object : ReleaseRepository {
            override suspend fun getRelease(releaseId: Int): ReleaseDetail =
                error("should not be called")
        }
        useCase = EnrichAlbumUseCase(noOpReleaseRepository, filterOptionsRepository)

        useCase(baseAlbum(year = 1992, labels = listOf("WEA"), mainReleaseId = null))

        val options = filterOptionsRepository.options.value
        assertTrue(1992 in options.years)
        assertTrue("WEA" in options.labels)
    }

    @Test
    fun `does not register any genres when mainReleaseId is null`() = runTest {
        val noOpReleaseRepository = object : ReleaseRepository {
            override suspend fun getRelease(releaseId: Int): ReleaseDetail =
                error("should not be called")
        }
        useCase = EnrichAlbumUseCase(noOpReleaseRepository, filterOptionsRepository)

        useCase(baseAlbum(mainReleaseId = null))

        assertTrue(filterOptionsRepository.options.value.genres.isEmpty())
    }

    // endregion

    // region edge cases

    @Test
    fun `year zero is not registered`() = runTest {
        val detail = ReleaseDetail(id = 100, genres = listOf("Rock"), labels = listOf("WEA"), imageUrl = "")
        useCase = EnrichAlbumUseCase(fakeReleaseRepository(detail), filterOptionsRepository)

        useCase(baseAlbum(year = 0))

        assertTrue(filterOptionsRepository.options.value.years.isEmpty())
    }

    // endregion
}
