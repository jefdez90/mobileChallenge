package com.discogs.mobilechallenge.data.cache

import com.discogs.mobilechallenge.domain.model.FilterOptions
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class FilterOptionsRepositoryImplTest {

    private lateinit var repository: FilterOptionsRepositoryImpl

    @Before
    fun setUp() {
        repository = FilterOptionsRepositoryImpl()
    }

    // region initial state

    @Test
    fun `initial state has empty options`() {
        assertEquals(FilterOptions(), repository.options.value)
    }

    // endregion

    // region reset

    @Test
    fun `reset clears all accumulated options`() {
        repository.addYear(1994)
        repository.addLabel("Warner")
        repository.addGenre("Rock")

        repository.reset()

        assertEquals(FilterOptions(), repository.options.value)
    }

    // endregion

    // region addYear

    @Test
    fun `addYear adds valid year`() {
        repository.addYear(2003)

        assertEquals(setOf(2003), repository.options.value.years)
    }

    @Test
    fun `addYear ignores null`() {
        repository.addYear(null)

        assertEquals(emptySet<Int>(), repository.options.value.years)
    }

    @Test
    fun `addYear ignores zero`() {
        repository.addYear(0)

        assertEquals(emptySet<Int>(), repository.options.value.years)
    }

    @Test
    fun `addYear ignores negative value`() {
        repository.addYear(-1)

        assertEquals(emptySet<Int>(), repository.options.value.years)
    }

    @Test
    fun `addYear same year twice is stored once`() {
        repository.addYear(1994)
        repository.addYear(1994)

        assertEquals(setOf(1994), repository.options.value.years)
    }

    @Test
    fun `addYear accumulates multiple distinct years`() {
        repository.addYear(1992)
        repository.addYear(1994)
        repository.addYear(2003)

        assertEquals(setOf(1992, 1994, 2003), repository.options.value.years)
    }

    // endregion

    // region addLabel

    @Test
    fun `addLabel adds valid label`() {
        repository.addLabel("Warner")

        assertEquals(setOf("Warner"), repository.options.value.labels)
    }

    @Test
    fun `addLabel ignores null`() {
        repository.addLabel(null)

        assertEquals(emptySet<String>(), repository.options.value.labels)
    }

    @Test
    fun `addLabel ignores blank string`() {
        repository.addLabel("   ")

        assertEquals(emptySet<String>(), repository.options.value.labels)
    }

    @Test
    fun `addLabel ignores empty string`() {
        repository.addLabel("")

        assertEquals(emptySet<String>(), repository.options.value.labels)
    }

    @Test
    fun `addLabel same label twice is stored once`() {
        repository.addLabel("Universal")
        repository.addLabel("Universal")

        assertEquals(setOf("Universal"), repository.options.value.labels)
    }

    @Test
    fun `addLabel accumulates multiple distinct labels`() {
        repository.addLabel("Warner")
        repository.addLabel("Universal")

        assertEquals(setOf("Warner", "Universal"), repository.options.value.labels)
    }

    // endregion

    // region addGenre

    @Test
    fun `addGenre adds valid genre`() {
        repository.addGenre("Rock")

        assertEquals(setOf("Rock"), repository.options.value.genres)
    }

    @Test
    fun `addGenre ignores null`() {
        repository.addGenre(null)

        assertEquals(emptySet<String>(), repository.options.value.genres)
    }

    @Test
    fun `addGenre ignores blank string`() {
        repository.addGenre("   ")

        assertEquals(emptySet<String>(), repository.options.value.genres)
    }

    @Test
    fun `addGenre ignores empty string`() {
        repository.addGenre("")

        assertEquals(emptySet<String>(), repository.options.value.genres)
    }

    @Test
    fun `addGenre same genre twice is stored once`() {
        repository.addGenre("Latin")
        repository.addGenre("Latin")

        assertEquals(setOf("Latin"), repository.options.value.genres)
    }

    @Test
    fun `addGenre accumulates multiple distinct genres`() {
        repository.addGenre("Rock")
        repository.addGenre("Latin")

        assertEquals(setOf("Rock", "Latin"), repository.options.value.genres)
    }

    // endregion

    // region combined

    @Test
    fun `all three categories accumulate independently`() {
        repository.addYear(1994)
        repository.addLabel("Warner")
        repository.addGenre("Rock")

        val options = repository.options.value
        assertEquals(setOf(1994), options.years)
        assertEquals(setOf("Warner"), options.labels)
        assertEquals(setOf("Rock"), options.genres)
    }

    @Test
    fun `reset after accumulation restores empty state`() {
        repository.addYear(2003)
        repository.addLabel("Universal")
        repository.addGenre("Latin")
        repository.reset()

        repository.addYear(1992)

        val options = repository.options.value
        assertEquals(setOf(1992), options.years)
        assertEquals(emptySet<String>(), options.labels)
        assertEquals(emptySet<String>(), options.genres)
    }

    // endregion
}
