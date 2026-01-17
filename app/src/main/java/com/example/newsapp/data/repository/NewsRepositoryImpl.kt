package com.example.newsapp.data.repository

import com.example.newsapp.data.api.NewsApiService
import com.example.newsapp.data.db.dao.ArticleDao
import com.example.newsapp.data.toArticleEntity
import com.example.newsapp.data.toDomainArticle
import com.example.newsapp.domain.model.Article
import com.example.newsapp.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * API Key for the News API.
 *
 * NOTE: In a production app, API keys should NEVER be hardcoded!
 * Better alternatives:
 * 1. Store in local.properties (not committed to git)
 * 2. Use BuildConfig fields
 * 3. Fetch from secure backend
 * 4. Use Android Keystore for sensitive data
 */
private const val API_KEY = "4a0b193d52db4ffebdeee6c47a28afdd"

/**
 * Implementation of the NewsRepository interface.
 *
 * WHAT IS A REPOSITORY?
 * The Repository pattern provides a clean API for data access to the rest of
 * the application. It abstracts the data sources (API, database) from the
 * domain layer.
 *
 * THIS REPOSITORY COORDINATES TWO DATA SOURCES:
 * 1. Remote: News API (via Retrofit) - For fetching fresh news
 * 2. Local: Room Database (via DAO) - For storing/retrieving favorites
 *
 * WHY USE THE REPOSITORY PATTERN?
 * - Single source of truth for data
 * - Hides implementation details from domain layer
 * - Easy to add caching, offline support, or switch data sources
 * - Makes testing easier (can mock the repository)
 *
 * DATA FLOW:
 * ViewModel → Use Case → Repository → [API or Database]
 *                                   ↓
 *                              DTO/Entity
 *                                   ↓
 *                          (Mapper functions)
 *                                   ↓
 *                            Domain Model (Article)
 *
 * @param apiService Retrofit service for News API calls.
 * @param articleDao Room DAO for database operations.
 */
@Singleton  // Ensures only one instance exists throughout the app
class NewsRepositoryImpl @Inject constructor(
    private val apiService: NewsApiService,
    private val articleDao: ArticleDao
) : NewsRepository {

    /**
     * Fetches top news headlines from the remote API.
     *
     * NETWORK CALL FLOW:
     * 1. Call Retrofit service (suspends until response)
     * 2. Retrofit makes HTTP request to News API
     * 3. Response is automatically deserialized to NewsResponse
     * 4. We extract articles and map DTOs to domain models
     *
     * @param page Page number for pagination (starts from 1).
     * @param pageSize Number of articles to fetch per page.
     * @return List of Article domain models.
     * @throws Exception if network call fails.
     */
    override suspend fun getTopHeadlines(page: Int, pageSize: Int): List<Article> {
        // Make the API call - this suspends until we get a response
        val response = apiService.getTopHeadlines(
            apiKey = API_KEY,
            page = page,
            pageSize = pageSize
        )
        // Map each ArticleDto from the API to our domain Article model
        // This decouples our domain from the API's data structure
        return response.articles.map { it.toDomainArticle() }
    }

    /**
     * Gets a reactive stream of favorite articles from the local database.
     *
     * WHY FLOW?
     * - Room can return Flow<List<T>> which emits new data on changes
     * - When we insert/delete favorites, Room automatically emits updated list
     * - The UI observes this Flow and updates without manual refresh
     *
     * @return Flow that emits the list of favorites whenever it changes.
     */
    override fun getFavoriteArticles(): Flow<List<Article>> {
        return articleDao.getFavoriteArticles()
            .map { entities ->
                // Convert database entities to domain models
                entities.map { it.toDomainArticle() }
            }
    }

    /**
     * Saves an article to the favorites database.
     *
     * DATABASE OPERATION:
     * - Converts Article to ArticleEntity
     * - Inserts into Room database
     * - If article with same URL exists, it's replaced (REPLACE strategy)
     * - This triggers Flow emissions in getFavoriteArticles() and isFavorite()
     *
     * @param article The article to save (should have isFavorite = true).
     */
    override suspend fun saveArticle(article: Article) {
        // Convert domain model to database entity and insert
        articleDao.insertArticle(article.toArticleEntity())
    }

    /**
     * Removes an article from the favorites database.
     *
     * After deletion:
     * - getFavoriteArticles() Flow emits updated list (without this article)
     * - isFavorite() Flow emits false for this article's URL
     * - UI automatically updates through reactive observation
     *
     * @param article The article to remove from favorites.
     */
    override suspend fun deleteArticle(article: Article) {
        // Convert domain model to database entity and delete
        articleDao.deleteArticle(article.toArticleEntity())
    }

    /**
     * Checks if an article is in the favorites database.
     *
     * HOW IT WORKS:
     * - DAO returns Flow<Int> with count of matching rows
     * - We map this to Flow<Boolean> (count > 0 means favorited)
     * - This Flow emits new value whenever favorite status changes
     *
     * USED BY:
     * - DetailViewModel to show correct heart icon state
     * - The Flow ensures heart icon updates if favorite status changes
     *
     * @param url The unique URL of the article to check.
     * @return Flow that emits true if favorited, false otherwise.
     */
    override fun isFavorite(url: String): Flow<Boolean> {
        return articleDao.isFavorite(url)
            .map { count ->
                // If count > 0, the article exists in favorites table
                count > 0
            }
    }
}
