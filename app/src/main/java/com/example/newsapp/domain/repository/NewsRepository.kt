package com.example.newsapp.domain.repository

import com.example.newsapp.domain.model.Article
import kotlinx.coroutines.flow.Flow

/**
 * This interface defines the contract for the news repository.
 * It abstracts the data sources and provides a clean API for the domain layer to interact with.
 * The repository is responsible for fetching news and managing favorite articles.
 */
interface NewsRepository {

    /**
     * Fetches top headline articles from the data source.
     * @param page The page number for pagination.
     * @param pageSize The number of articles per page.
     * @return A list of Article domain models.
     */
    suspend fun getTopHeadlines(page: Int, pageSize: Int): List<Article>

    /**
     * Retrieves all favorite articles from the local database.
     * @return A Flow that emits a list of favorite articles.
     */
    fun getFavoriteArticles(): Flow<List<Article>>

    /**
     * Saves an article as a favorite in the local database.
     * @param article The article to be saved.
     */
    suspend fun saveArticle(article: Article)

    /**
     * Deletes an article from the favorites in the local database.
     * @param article The article to be deleted.
     */
    suspend fun deleteArticle(article: Article)

    /**
     * Checks if an article is marked as a favorite.
     * @param url The unique URL of the article.
     * @return A Flow that emits true if the article is a favorite, false otherwise.
     */
    fun isFavorite(url: String): Flow<Boolean>
}
