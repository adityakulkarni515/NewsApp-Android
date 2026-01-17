package com.example.newsapp.data.db.dao

import androidx.room.*
import com.example.newsapp.data.db.entity.ArticleEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for the 'articles' table.
 *
 * WHAT IS A DAO?
 * DAO stands for Data Access Object. It's a pattern used in Room database
 * where you define methods for accessing the database. Room generates the
 * actual SQL implementation at compile time.
 *
 * HOW ROOM WORKS:
 * 1. You define an interface with @Dao annotation
 * 2. Add methods with @Query, @Insert, @Delete, @Update annotations
 * 3. Room generates the implementation at compile time
 * 4. You inject this DAO where needed (repository)
 *
 * SUSPEND VS REGULAR FUNCTIONS:
 * - suspend functions (insertArticle, deleteArticle): For write operations
 *   These run on background thread and suspend until complete
 * - Regular functions returning Flow: For read operations with live updates
 *   Room observes the table and emits new data when it changes
 *
 * WHY RETURN FLOW?
 * When you return Flow<List<T>> from a DAO query:
 * - Room watches the underlying table for changes
 * - When data changes (insert/delete), it automatically re-runs the query
 * - The Flow emits the new result
 * - Your UI updates automatically without manual refresh
 */
@Dao
interface ArticleDao {

    /**
     * Inserts or updates an article in the database.
     *
     * OnConflictStrategy.REPLACE means:
     * - If article with same URL exists → Replace it with new data
     * - If article doesn't exist → Insert as new row
     *
     * This is useful for favorites because:
     * - User favorites an article → Insert new row
     * - User re-favorites same article → Just update (no duplicates)
     *
     * @param article The ArticleEntity to insert or update.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(article: ArticleEntity)

    /**
     * Deletes an article from the database.
     *
     * Room matches the article by its primary key (url field).
     * After deletion, any Flow observing this table will emit updated data.
     *
     * @param article The ArticleEntity to delete.
     */
    @Delete
    suspend fun deleteArticle(article: ArticleEntity)

    /**
     * Retrieves all favorite articles from the database.
     *
     * THE QUERY EXPLAINED:
     * SELECT * FROM articles WHERE isFavorite = 1
     * - SELECT * : Get all columns
     * - FROM articles : From the articles table
     * - WHERE isFavorite = 1 : Only where isFavorite is true (1 in SQLite)
     *
     * Returns Flow<List<ArticleEntity>> which means:
     * - Room observes the articles table
     * - When any row changes (insert/update/delete), it re-runs this query
     * - Emits the new list to all observers
     *
     * @return A Flow that emits the current list of favorites whenever it changes.
     */
    @Query("SELECT * FROM articles WHERE isFavorite = 1")
    fun getFavoriteArticles(): Flow<List<ArticleEntity>>

    /**
     * Checks if a specific article exists in the favorites.
     *
     * THE QUERY EXPLAINED:
     * SELECT COUNT(*) FROM articles WHERE url = :url AND isFavorite = 1
     * - COUNT(*) : Returns the number of matching rows (0 or 1)
     * - url = :url : Match the provided URL parameter
     * - isFavorite = 1 : Only count if it's a favorite
     *
     * WHY COUNT INSTEAD OF SELECT?
     * - More efficient: Just counts, doesn't fetch all columns
     * - Simple boolean conversion: count > 0 means exists
     *
     * The Flow emits new count whenever the matching row changes.
     *
     * @param url The unique URL of the article to check.
     * @return Flow that emits 0 (not favorite) or 1 (is favorite).
     */
    @Query("SELECT COUNT(*) FROM articles WHERE url = :url AND isFavorite = 1")
    fun isFavorite(url: String): Flow<Int>
}
