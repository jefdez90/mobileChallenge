package com.discogs.mobilechallenge.data.repository

import com.discogs.mobilechallenge.data.remote.api.DiscogsApi
import com.discogs.mobilechallenge.domain.model.ReleaseDetail
import com.discogs.mobilechallenge.domain.repository.ReleaseRepository
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReleaseRepositoryImpl @Inject constructor(
    private val api: DiscogsApi,
) : ReleaseRepository {

    private val cache = ConcurrentHashMap<Int, ReleaseDetail>()

    override suspend fun getRelease(releaseId: Int): ReleaseDetail {
        cache[releaseId]?.let { return it }
        val detail = api.getReleaseDetail(releaseId).toReleaseDetail()
        cache[releaseId] = detail
        return detail
    }
}
