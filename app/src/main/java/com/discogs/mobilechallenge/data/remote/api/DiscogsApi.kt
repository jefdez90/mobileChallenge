package com.discogs.mobilechallenge.data.remote.api

import com.discogs.mobilechallenge.data.remote.dto.SearchResponseDto
import com.discogs.mobilechallenge.data.repository.DiscogsRepositoryImpl.Companion.PAGE_SIZE
import retrofit2.http.GET
import retrofit2.http.Query

interface DiscogsApi {

    @GET("database/search")
    suspend fun searchArtists(
        @Query("q") query: String,
        @Query("type") type: String = "artist",
        @Query("page") page: Int,
        @Query("per_page") perPage: Int = PAGE_SIZE,
    ): SearchResponseDto

}
