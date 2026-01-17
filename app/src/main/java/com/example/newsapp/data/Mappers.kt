package com.example.newsapp.data

import com.example.newsapp.data.db.entity.ArticleEntity
import com.example.newsapp.data.model.ArticleDto
import com.example.newsapp.domain.model.Article

/**
 * ============================================================================
 * MAPPER FUNCTIONS - Converting Between Data Layer and Domain Layer
 * ============================================================================
 *
 * WHAT ARE MAPPERS?
 * Mappers are functions that convert objects from one type to another.
 * In Clean Architecture, we use mappers to convert between:
 * - DTOs (Data Transfer Objects from API)
 * - Entities (Database objects)
 * - Domain Models (Business objects used throughout the app)
 *
 * WHY USE MAPPERS?
 * 1. Decoupling: Domain layer doesn't depend on data layer structure
 * 2. Flexibility: Can change API/DB structure without affecting business logic
 * 3. Safety: Can handle null values and provide defaults
 * 4. Clarity: Each layer has its own representation of data
 *
 * DATA FLOW IN THIS APP:
 *
 * From API:
 * API Response → ArticleDto → (mapper) → Article (Domain)
 *
 * From Database:
 * Room DB → ArticleEntity → (mapper) → Article (Domain)
 *
 * To Database:
 * Article (Domain) → (mapper) → ArticleEntity → Room DB
 *
 * EXTENSION FUNCTIONS:
 * We use Kotlin extension functions (fun Type.functionName()) which allows
 * calling the mapper like: articleDto.toDomainArticle() instead of
 * Mapper.toDomainArticle(articleDto)
 */

/**
 * Converts an ArticleDto (API response) to an Article (Domain Model).
 *
 * WHEN IS THIS USED?
 * - In NewsRepositoryImpl.getTopHeadlines()
 * - After fetching news from the API, we map each DTO to domain model
 *
 * WHY NOT USE DTO DIRECTLY?
 * - DTO structure is controlled by the API (can change)
 * - We don't want API changes to break our app
 * - Domain model has only what our app needs
 *
 * @receiver ArticleDto The API response object to convert.
 * @return Article domain model with mapped fields.
 */
fun ArticleDto.toDomainArticle(): Article {
    return Article(
        // Handle null URL with empty string fallback
        // URL should never be null in real data, but API might return null
        url = url ?: "",

        // Extract source name from nested SourceDto object
        // API returns: { "source": { "id": "...", "name": "BBC News" } }
        // We only need the name
        sourceName = source.name,

        // Direct mappings - these can be null in the API response
        author = author,
        title = title,
        description = description,
        urlToImage = urlToImage,
        publishedAt = publishedAt,
        content = content

        // Note: isFavorite defaults to false for new articles from API
    )
}

/**
 * Converts an ArticleEntity (Database) to an Article (Domain Model).
 *
 * WHEN IS THIS USED?
 * - In NewsRepositoryImpl.getFavoriteArticles()
 * - When reading favorites from the database, we map entities to domain models
 *
 * @receiver ArticleEntity The database entity to convert.
 * @return Article domain model with mapped fields including favorite status.
 */
fun ArticleEntity.toDomainArticle(): Article {
    return Article(
        url = url,
        sourceName = sourceName,
        author = author,
        title = title,
        description = description,
        urlToImage = urlToImage,
        publishedAt = publishedAt,
        content = content,
        // Important: Preserve the favorite status from database
        // This is what makes favorites persist across app restarts
        isFavorite = isFavorite
    )
}

/**
 * Converts an Article (Domain Model) to an ArticleEntity (Database).
 *
 * WHEN IS THIS USED?
 * - In NewsRepositoryImpl.saveArticle() - When saving to favorites
 * - In NewsRepositoryImpl.deleteArticle() - When removing from favorites
 *
 * @receiver Article The domain model to convert.
 * @return ArticleEntity ready to be saved to Room database.
 */
fun Article.toArticleEntity(): ArticleEntity {
    return ArticleEntity(
        url = url,
        sourceName = sourceName,
        author = author,
        title = title,
        description = description,
        urlToImage = urlToImage,
        publishedAt = publishedAt,
        content = content,
        // Preserve the favorite status when saving
        // When favoriting: this will be true
        // When unfavoriting: this will be false (but we delete anyway)
        isFavorite = isFavorite
    )
}
