package com.example.newsapp.di

import com.example.newsapp.data.repository.NewsRepositoryImpl
import com.example.newsapp.domain.repository.NewsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * This Hilt module is responsible for providing repository implementations.
 * Using @Binds is more efficient than @Provides for simple interface-to-implementation bindings.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    /**
     * Binds the NewsRepository interface to its implementation, NewsRepositoryImpl.
     * This tells Hilt to provide an instance of NewsRepositoryImpl whenever NewsRepository is requested.
     * @param newsRepositoryImpl The implementation of the repository.
     * @return An instance of NewsRepository.
     */
    @Binds
    @Singleton
    abstract fun bindNewsRepository(newsRepositoryImpl: NewsRepositoryImpl): NewsRepository
}
