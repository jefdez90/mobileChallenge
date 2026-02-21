package com.discogs.mobilechallenge.core.di

import com.discogs.mobilechallenge.data.cache.FilterOptionsRepositoryImpl
import com.discogs.mobilechallenge.data.repository.DiscogsRepositoryImpl
import com.discogs.mobilechallenge.domain.repository.DiscogsRepository
import com.discogs.mobilechallenge.domain.repository.FilterOptionsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindDiscogsRepository(impl: DiscogsRepositoryImpl): DiscogsRepository

    @Binds
    @Singleton
    abstract fun bindFilterOptionsRepository(impl: FilterOptionsRepositoryImpl): FilterOptionsRepository
}
