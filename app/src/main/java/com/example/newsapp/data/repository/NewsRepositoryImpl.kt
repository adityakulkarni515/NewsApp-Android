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

private const val API_KEY = "4a0b193d52db4ffebdeee6c47a28afdd"

@Singleton
class NewsRepositoryImpl @Inject constructor(
    private val apiService: NewsApiService,
    private val articleDao: ArticleDao
) : NewsRepository {

    override suspend fun getTopHeadlines(page: Int, pageSize: Int): List<Article> {
        val response = apiService.getTopHeadlines(
            apiKey = API_KEY,
            page = page,
            pageSize = pageSize
        )
        return response.articles.map { it.toDomainArticle() }
    }

    override fun getFavoriteArticles(): Flow<List<Article>> {
        return articleDao.getFavoriteArticles()
            .map { entities -> entities.map { it.toDomainArticle() } }
    }

    override suspend fun saveArticle(article: Article) {
        articleDao.insertArticle(article.toArticleEntity())
    }

    override suspend fun deleteArticle(article: Article) {
        articleDao.deleteArticle(article.toArticleEntity())
    }

    override fun isFavorite(url: String): Flow<Boolean> {
        return articleDao.isFavorite(url).map { count -> count > 0 }
    }
}
