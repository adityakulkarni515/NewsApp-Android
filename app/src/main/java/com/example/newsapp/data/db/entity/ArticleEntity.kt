package com.example.newsapp.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room database entity representing a news article.
 *
 * WHAT IS AN ENTITY?
 * In Room database, an Entity represents a table in the SQLite database.
 * Each instance of this class represents a row in the table.
 * The properties become columns in the table.
 *
 * TABLE STRUCTURE CREATED BY ROOM:
 * ┌──────────────────────────────────────────────────────────────────┐
 * │                        articles table                            │
 * ├────────────┬────────────┬────────────┬──────────┬───────────────┤
 * │ url (PK)   │ sourceName │ author     │ title    │ isFavorite    │
 * │ TEXT       │ TEXT       │ TEXT       │ TEXT     │ INTEGER (0/1) │
 * └────────────┴────────────┴────────────┴──────────┴───────────────┘
 *
 * WHY USE URL AS PRIMARY KEY?
 * - Each news article has a unique URL
 * - The News API doesn't provide a separate ID field
 * - URL is guaranteed to be unique per article
 * - Prevents duplicate entries (same article can't be favorited twice)
 *
 * ENTITY VS DOMAIN MODEL:
 * - ArticleEntity: Represents how data is stored in database
 * - Article (domain): Represents how app uses the data
 * - We use mapper functions to convert between them
 * - This separation allows changing database structure without affecting app logic
 *
 * @property url The unique URL of the article (Primary Key).
 * @property sourceName The name of the news source (e.g., "BBC News").
 * @property author The author who wrote the article.
 * @property title The headline/title of the article.
 * @property description A brief description/summary.
 * @property urlToImage URL of the article's main image.
 * @property publishedAt Publication date as a string.
 * @property content The full article content.
 * @property isFavorite True if user has favorited this article.
 */
@Entity(tableName = "articles")  // Creates table named "articles"
data class ArticleEntity(
    /**
     * The URL serves as the primary key (unique identifier).
     * Room will create an index on this column for fast lookups.
     * Using natural key (URL) instead of auto-generated ID.
     */
    @PrimaryKey
    val url: String,

    /**
     * Name of the news source (e.g., "Reuters", "CNN", "BBC News").
     * Nullable because some articles might not have source info.
     */
    val sourceName: String?,

    /**
     * Author of the article.
     * Nullable because some articles don't have author information.
     */
    val author: String?,

    /**
     * The headline/title of the article.
     * This is what users see in the list view.
     */
    val title: String?,

    /**
     * A short description or summary of the article.
     * Usually the first paragraph or a teaser.
     */
    val description: String?,

    /**
     * URL pointing to the article's main image.
     * Used by Coil to load and display the image.
     */
    val urlToImage: String?,

    /**
     * Publication date and time as a string.
     * Format varies by API (usually ISO 8601).
     */
    val publishedAt: String?,

    /**
     * The full content of the article.
     * Note: News API truncates content for free tier.
     */
    val content: String?,

    /**
     * Flag indicating if user has favorited this article.
     * - true (1 in SQLite): Article is in favorites
     * - false (0 in SQLite): Article is not favorited
     * Default is false because articles are not favorited initially.
     */
    val isFavorite: Boolean = false
)
