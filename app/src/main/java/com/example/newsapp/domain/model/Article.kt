package com.example.newsapp.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Represents the core domain model for a news article.
 *
 * WHAT IS A DOMAIN MODEL?
 * In Clean Architecture, the domain layer contains the business logic and
 * business objects (like this Article class). It is independent of:
 * - Data sources (API responses, database entities)
 * - UI frameworks (Compose, Views)
 * - Android framework (except minimal dependencies like Parcelable)
 *
 * WHY SEPARATE FROM DATA LAYER?
 * 1. The API might return different field names (we map them in ArticleDto)
 * 2. The database might store differently (we map in ArticleEntity)
 * 3. This class represents what the app cares about, not how it's stored
 *
 * PARCELABLE EXPLAINED:
 * - Parcelable allows objects to be passed between Android components
 * - @Parcelize generates the boilerplate code automatically
 * - We use it to pass Article between screens via navigation
 *
 * THE URL AS IDENTIFIER:
 * The 'url' field serves as a unique identifier for each article because:
 * - Each news article has a unique URL
 * - The News API doesn't provide a separate ID field
 * - We use it as the primary key in the database
 *
 * @property url The unique URL of the article (used as identifier).
 * @property sourceName The name of the news source (e.g., "BBC News", "CNN").
 * @property author The author who wrote the article.
 * @property title The headline or title of the article.
 * @property description A brief summary/description of the article.
 * @property urlToImage URL of the article's main image.
 * @property publishedAt The publication date/time as a string.
 * @property content The full article content (may be truncated by API).
 * @property isFavorite True if the user has saved this article to favorites.
 */
@Parcelize
data class Article(
    val url: String,
    val sourceName: String?,
    val author: String?,
    val title: String?,
    val description: String?,
    val urlToImage: String?,
    val publishedAt: String?,
    val content: String?,
    val isFavorite: Boolean = false  // Changed from var to val for immutability
) : Parcelable
