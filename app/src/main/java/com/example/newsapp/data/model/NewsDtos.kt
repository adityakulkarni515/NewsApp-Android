package com.example.newsapp.data.model

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) for the response from the News API.
 * This class models the top-level structure of the API's JSON response.
 * @property status The status of the API request (e.g., "ok").
 * @property totalResults The total number of results available for the request.
 * @property articles The list of article DTOs.
 */
data class NewsResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("totalResults")
    val totalResults: Int,
    @SerializedName("articles")
    val articles: List<ArticleDto>
)

/**
 * Data Transfer Object (DTO) for a single news article from the API.
 * This class models the fields of a news article object in the API's JSON response.
 * @property source The source of the article.
 * @property author The author of the article.
 * @property title The headline or title of the article.
 * @property description A short description or snippet from the article.
 * @property url The direct URL to the article.
 * @property urlToImage The URL of an image associated with the article.
 * @property publishedAt The date and time the article was published, in UTC format (e.g., "2023-01-01T12:00:00Z").
 * @property content The full content of the article.
 */
data class ArticleDto(
    @SerializedName("source")
    val source: SourceDto,
    @SerializedName("author")
    val author: String?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("url")
    val url: String?,
    @SerializedName("urlToImage")
    val urlToImage: String?,
    @SerializedName("publishedAt")
    val publishedAt: String?,
    @SerializedName("content")
    val content: String?
)

/**
 * Data Transfer Object (DTO) for the source of a news article.
 * This class models the 'source' object nested within an article object.
 * @property id The unique identifier for the news source (can be null).
 * @property name The name of the news source (e.g., "Reuters").
 */
data class SourceDto(
    @SerializedName("id")
    val id: String?,
    @SerializedName("name")
    val name: String?
)
