package com.discogs.mobilechallenge.domain.filter

import com.discogs.mobilechallenge.domain.model.Album
import org.junit.Assert.assertEquals
import org.junit.Test

class AlbumFilterTest {

    private val albums = listOf(
        Album(id = 1, title = "Re", year = 1994, genres = listOf("Rock", "Latin"), labels = listOf("Warner"), imageUrl = ""),
        Album(id = 2, title = "Cuatro Caminos", year = 2003, genres = listOf("Rock", "Latin"), labels = listOf("Universal"), imageUrl = ""),
        Album(id = 3, title = "Sino", year = 1992, genres = listOf("Rock"), labels = listOf("Warner"), imageUrl = ""),
        Album(id = 4, title = "Avalancha de Éxitos", year = 1996, genres = listOf("Latin"), labels = listOf("Warner"), imageUrl = ""),
    )

    @Test
    fun `empty filter returns all albums`() {
        val result = albums.applyFilter(AlbumFilter())
        assertEquals(albums, result)
    }

    @Test
    fun `filter by year returns matching albums`() {
        val result = albums.applyFilter(AlbumFilter(year = 2003))
        assertEquals(listOf(albums[1]), result)
    }

    @Test
    fun `filter by year with no match returns empty list`() {
        val result = albums.applyFilter(AlbumFilter(year = 2000))
        assertEquals(emptyList<Album>(), result)
    }

    @Test
    fun `filter by genre returns albums containing genre`() {
        val result = albums.applyFilter(AlbumFilter(genre = "Latin"))
        assertEquals(listOf(albums[0], albums[1], albums[3]), result)
    }

    @Test
    fun `filter by label returns albums from label`() {
        val result = albums.applyFilter(AlbumFilter(label = "Warner"))
        assertEquals(listOf(albums[0], albums[2], albums[3]), result)
    }

    @Test
    fun `filter by year and genre applies both conditions`() {
        val result = albums.applyFilter(AlbumFilter(year = 1994, genre = "Latin"))
        assertEquals(listOf(albums[0]), result)
    }

    @Test
    fun `filter by all three criteria`() {
        val result = albums.applyFilter(AlbumFilter(year = 1994, genre = "Rock", label = "Warner"))
        assertEquals(listOf(albums[0]), result)
    }

    @Test
    fun `filter by all three criteria with no match returns empty list`() {
        val result = albums.applyFilter(AlbumFilter(year = 1994, genre = "Rock", label = "Universal"))
        assertEquals(emptyList<Album>(), result)
    }

    @Test
    fun `isEmpty is true for default filter`() {
        assertEquals(true, AlbumFilter().isEmpty)
    }

    @Test
    fun `isEmpty is false when any field is set`() {
        assertEquals(false, AlbumFilter(year = 2003).isEmpty)
        assertEquals(false, AlbumFilter(genre = "Rock").isEmpty)
        assertEquals(false, AlbumFilter(label = "Warner").isEmpty)
    }
}
