package com.example.newsapp.data

import com.example.newsapp.data.db.entity.ArticleEntity
import com.example.newsapp.data.model.ArticleDto
import com.example.newsapp.domain.model.Article

fun ArticleDto.toDomainArticle(): Article {
    return Article(
        url = url ?: "",
        sourceName = source.name,
        author = author,
        title = title,
        description = description,
        urlToImage = urlToImage,
        publishedAt = publishedAt,
        content = content
    )
}

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
        isFavorite = isFavorite
    )
}

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
        isFavorite = isFavorite
    )
}
