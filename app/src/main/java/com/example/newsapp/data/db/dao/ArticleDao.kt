package com.example.newsapp.data.db.dao

import androidx.room.*
import com.example.newsapp.data.db.entity.ArticleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(article: ArticleEntity)

    @Delete
    suspend fun deleteArticle(article: ArticleEntity)

    @Query("SELECT * FROM articles WHERE isFavorite = 1")
    fun getFavoriteArticles(): Flow<List<ArticleEntity>>

    @Query("SELECT COUNT(*) FROM articles WHERE url = :url AND isFavorite = 1")
    fun isFavorite(url: String): Flow<Int>
}
