package com.discogs.mobilechallenge.domain.repository

import com.discogs.mobilechallenge.domain.model.ReleaseDetail

interface ReleaseRepository {
    suspend fun getRelease(releaseId: Int): ReleaseDetail
}
