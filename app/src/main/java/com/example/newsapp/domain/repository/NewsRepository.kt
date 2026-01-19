package com.example.newsapp.domain.repository

import com.example.newsapp.domain.model.Article
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    suspend fun getTopHeadlines(page: Int, pageSize: Int): List<Article>
    fun getFavoriteArticles(): Flow<List<Article>>
    suspend fun saveArticle(article: Article)
    suspend fun deleteArticle(article: Article)
    fun isFavorite(url: String): Flow<Boolean>
}
