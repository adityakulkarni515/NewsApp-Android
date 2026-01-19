package com.example.newsapp.domain.usecase

import com.example.newsapp.domain.model.Article
import com.example.newsapp.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTopHeadlinesUseCase @Inject constructor(
    private val newsRepository: NewsRepository
) {
    suspend operator fun invoke(page: Int, pageSize: Int): Result<List<Article>> {
        return runCatching {
            newsRepository.getTopHeadlines(page, pageSize)
        }
    }
}

class GetFavoriteArticlesUseCase @Inject constructor(
    private val newsRepository: NewsRepository
) {
    operator fun invoke(): Flow<List<Article>> {
        return newsRepository.getFavoriteArticles()
    }
}

class SaveArticleUseCase @Inject constructor(
    private val newsRepository: NewsRepository
) {
    suspend operator fun invoke(article: Article) {
        newsRepository.saveArticle(article)
    }
}

class DeleteArticleUseCase @Inject constructor(
    private val newsRepository: NewsRepository
) {
    suspend operator fun invoke(article: Article) {
        newsRepository.deleteArticle(article)
    }
}

class CheckFavoriteStatusUseCase @Inject constructor(
    private val newsRepository: NewsRepository
) {
    operator fun invoke(url: String): Flow<Boolean> {
        return newsRepository.isFavorite(url)
    }
}
