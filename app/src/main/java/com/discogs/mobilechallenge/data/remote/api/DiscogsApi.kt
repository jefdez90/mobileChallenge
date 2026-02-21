package com.discogs.mobilechallenge.data.remote.api

import com.discogs.mobilechallenge.data.remote.dto.ArtistDetailDto
import com.discogs.mobilechallenge.data.remote.dto.ArtistReleasesDto
import com.discogs.mobilechallenge.data.remote.dto.SearchResponseDto
import com.discogs.mobilechallenge.data.repository.DiscogsRepositoryImpl.Companion.PAGE_SIZE
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DiscogsApi {

    @GET("database/search")
    suspend fun searchArtists(
        @Query("q") query: String,
        @Query("type") type: String = "artist",
        @Query("page") page: Int,
        @Query("per_page") perPage: Int = PAGE_SIZE,
    ): SearchResponseDto

    @GET("artists/{artistId}")
    suspend fun getArtist(
        @Path("artistId") artistId: Int,
    ): ArtistDetailDto

    @GET("artists/{artistId}/releases")
    suspend fun getArtistReleases(
        @Path("artistId") artistId: Int,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int = PAGE_SIZE,
        @Query("sort") sort: String = "year",
        @Query("sort_order") sortOrder: String = "desc",
    ): ArtistReleasesDto

}
