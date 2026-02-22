package com.discogs.mobilechallenge.data.remote.dto

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ArtistReleaseItemDtoTest {

    private fun dto(
        type: String = "master",
        role: String = "Main",
        id: Int = 1,
        title: String = "Re",
        year: Int? = 1994,
        label: String? = "Warner",
        thumb: String? = "https://img.example.com/cover.jpg",
    ) = ArtistReleaseItemDto(
        id = id,
        title = title,
        year = year,
        type = type,
        role = role,
        label = label,
        thumb = thumb,
        mainRelease = null,
        resourceUrl = "https://api.discogs.com/masters/1",
    )

    // --- Domain rule: type and role guards ---

    @Test
    fun `returns null when type is not master`() {
        assertNull(dto(type = "release").toAlbumOrNull())
    }

    @Test
    fun `returns null when role is not Main`() {
        assertNull(dto(role = "Appearance").toAlbumOrNull())
    }

    @Test
    fun `returns null when both type and role are wrong`() {
        assertNull(dto(type = "release", role = "Appearance").toAlbumOrNull())
    }

    @Test
    fun `returns album when type is master and role is Main`() {
        val album = dto().toAlbumOrNull()
        assertEquals(1, album?.id)
    }

    // --- Field mapping ---

    @Test
    fun `maps id and title correctly`() {
        val album = dto(id = 42, title = "Cuatro Caminos").toAlbumOrNull()!!
        assertEquals(42, album.id)
        assertEquals("Cuatro Caminos", album.title)
    }

    @Test
    fun `maps year correctly`() {
        val album = dto(year = 2003).toAlbumOrNull()!!
        assertEquals(2003, album.year)
    }

    @Test
    fun `maps label into labels list`() {
        val album = dto(label = "Universal").toAlbumOrNull()!!
        assertEquals(listOf("Universal"), album.labels)
    }

    @Test
    fun `maps thumb into imageUrl`() {
        val album = dto(thumb = "https://img.example.com/cover.jpg").toAlbumOrNull()!!
        assertEquals("https://img.example.com/cover.jpg", album.imageUrl)
    }

    @Test
    fun `genres is always empty (pre-enrichment)`() {
        val album = dto().toAlbumOrNull()!!
        assertEquals(emptyList<String>(), album.genres)
    }

    // --- Edge cases ---

    @Test
    fun `null year maps to 0`() {
        val album = dto(year = null).toAlbumOrNull()!!
        assertEquals(0, album.year)
    }

    @Test
    fun `null label produces empty labels list`() {
        val album = dto(label = null).toAlbumOrNull()!!
        assertEquals(emptyList<String>(), album.labels)
    }

    @Test
    fun `blank label produces empty labels list`() {
        val album = dto(label = "   ").toAlbumOrNull()!!
        assertEquals(emptyList<String>(), album.labels)
    }

    @Test
    fun `null thumb produces empty imageUrl`() {
        val album = dto(thumb = null).toAlbumOrNull()!!
        assertEquals("", album.imageUrl)
    }
}
