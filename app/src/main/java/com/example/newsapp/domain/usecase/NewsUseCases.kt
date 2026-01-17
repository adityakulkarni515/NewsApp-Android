package com.example.newsapp.domain.usecase

import com.example.newsapp.domain.model.Article
import com.example.newsapp.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * ============================================================================
 * USE CASES - THE BUSINESS LOGIC LAYER
 * ============================================================================
 *
 * WHAT ARE USE CASES?
 * Use cases (also called Interactors) represent single actions that the user
 * can do in the app. Each use case encapsulates one specific business operation.
 *
 * WHY USE THE USE CASE PATTERN?
 * 1. Single Responsibility: Each class does one thing well
 * 2. Testability: Easy to unit test business logic in isolation
 * 3. Reusability: Same use case can be called from different ViewModels
 * 4. Abstraction: ViewModel doesn't need to know about Repository details
 *
 * HOW THEY FIT IN CLEAN ARCHITECTURE:
 * UI Layer (Compose) → ViewModel → Use Case → Repository → Data Sources
 *
 * THE INVOKE OPERATOR:
 * Using 'operator fun invoke()' allows calling the use case like a function:
 * Instead of: getTopHeadlinesUseCase.execute(page, pageSize)
 * We can write: getTopHeadlinesUseCase(page, pageSize)
 */

/**
 * Use case for fetching top news headlines from the API.
 *
 * This use case is called by HomeViewModel to get the list of news articles
 * to display on the home screen.
 *
 * @param newsRepository The repository that provides news data.
 */
class GetTopHeadlinesUseCase @Inject constructor(
    private val newsRepository: NewsRepository
) {
    /**
     * Fetches top headlines with pagination support.
     *
     * KOTLIN RESULT TYPE:
     * Returns Result<List<Article>> which can be either:
     * - Result.success(articles) - When API call succeeds
     * - Result.failure(exception) - When API call fails
     *
     * This allows the ViewModel to handle success/error states cleanly.
     *
     * @param page The page number for pagination (starts from 1).
     * @param pageSize The number of articles per page.
     * @return Result wrapping the list of articles or an exception.
     */
    suspend operator fun invoke(page: Int, pageSize: Int): Result<List<Article>> {
        // runCatching is a Kotlin function that:
        // 1. Executes the code block
        // 2. If successful, returns Result.success(value)
        // 3. If exception thrown, returns Result.failure(exception)
        return runCatching {
            newsRepository.getTopHeadlines(page, pageSize)
        }
    }
}

/**
 * Use case for getting the list of favorite articles.
 *
 * This use case is called by FavoritesViewModel to display saved articles.
 * It returns a Flow so the UI updates automatically when favorites change.
 *
 * @param newsRepository The repository that provides favorite articles.
 */
class GetFavoriteArticlesUseCase @Inject constructor(
    private val newsRepository: NewsRepository
) {
    /**
     * Returns a Flow of favorite articles.
     *
     * WHY RETURN A FLOW?
     * - Flow is reactive: emits new data when database changes
     * - When user adds/removes favorite in DetailScreen, this Flow emits updated list
     * - FavoritesScreen automatically updates without manual refresh
     *
     * @return A Flow that emits the current list of favorites whenever it changes.
     */
    operator fun invoke(): Flow<List<Article>> {
        return newsRepository.getFavoriteArticles()
    }
}

/**
 * Use case for saving an article to favorites.
 *
 * This use case is called by DetailViewModel when user taps the heart icon
 * to add an article to their favorites.
 *
 * @param newsRepository The repository that handles database operations.
 */
class SaveArticleUseCase @Inject constructor(
    private val newsRepository: NewsRepository
) {
    /**
     * Saves the article to the Room database as a favorite.
     *
     * SUSPEND FUNCTION:
     * This is a suspend function because database operations should not
     * run on the main thread. The caller (ViewModel) launches a coroutine.
     *
     * @param article The article to save (with isFavorite = true).
     */
    suspend operator fun invoke(article: Article) {
        newsRepository.saveArticle(article)
    }
}

/**
 * Use case for removing an article from favorites.
 *
 * This use case is called by DetailViewModel when user taps the heart icon
 * to remove an article from their favorites.
 *
 * @param newsRepository The repository that handles database operations.
 */
class DeleteArticleUseCase @Inject constructor(
    private val newsRepository: NewsRepository
) {
    /**
     * Removes the article from the Room database.
     *
     * After deletion:
     * 1. The GetFavoriteArticlesUseCase Flow emits updated list
     * 2. FavoritesScreen automatically removes the item from UI
     * 3. CheckFavoriteStatusUseCase Flow emits false
     * 4. DetailScreen heart icon becomes unfilled
     *
     * @param article The article to remove from favorites.
     */
    suspend operator fun invoke(article: Article) {
        newsRepository.deleteArticle(article)
    }
}

/**
 * Use case for checking if an article is in the favorites list.
 *
 * This use case is called by DetailViewModel to determine whether to show
 * a filled or unfilled heart icon.
 *
 * @param newsRepository The repository that handles database queries.
 */
class CheckFavoriteStatusUseCase @Inject constructor(
    private val newsRepository: NewsRepository
) {
    /**
     * Returns a Flow that emits the favorite status of an article.
     *
     * WHY RETURN A FLOW?
     * - Real-time updates: If favorite status changes, Flow emits new value
     * - Consistency: DetailScreen always shows correct heart state
     * - Works across screens: Changes in one screen reflect in others
     *
     * @param url The unique URL of the article to check.
     * @return Flow that emits true if article is favorited, false otherwise.
     */
    operator fun invoke(url: String): Flow<Boolean> {
        return newsRepository.isFavorite(url)
    }
}
