package com.example.newsapp.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.domain.model.Article
import com.example.newsapp.domain.usecase.GetFavoriteArticlesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

/**
 * Represents the UI state for the Favorites screen.
 *
 * This data class follows the "single state object" pattern where all UI state
 * is contained in one immutable object. Benefits:
 * - Easy to track what data the screen needs
 * - Simple state updates via copy()
 * - Works well with Compose's recomposition system
 *
 * @property articles The list of favorite news articles to display.
 *                    Empty list by default (shows "no favorites" message).
 */
data class FavoritesState(
    val articles: List<Article> = emptyList()
)

private const val TAG = "FavoritesViewModel"

/**
 * ViewModel for the Favorites screen.
 *
 * This ViewModel manages the list of articles the user has saved as favorites.
 * It demonstrates reactive programming with Kotlin Flows:
 *
 * HOW REACTIVE UPDATES WORK:
 * 1. Room database stores favorite articles
 * 2. When queried with Flow, Room emits new data whenever the table changes
 * 3. DetailViewModel saves/deletes articles → Room table changes
 * 4. Room automatically emits new list → This ViewModel receives it
 * 5. State updates → Compose recomposes FavoritesScreen → UI updates
 *
 * This means:
 * - No manual refresh needed
 * - Real-time synchronization between screens
 * - Removing a favorite in DetailScreen instantly updates this list
 *
 * LIFECYCLE:
 * - Created when FavoritesScreen is first displayed
 * - Survives configuration changes (screen rotation)
 * - Destroyed when user navigates away (unless using navigation-scoped ViewModel)
 *
 * @param getFavoriteArticlesUseCase The use case that provides the Flow of favorite articles.
 */
@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getFavoriteArticlesUseCase: GetFavoriteArticlesUseCase
) : ViewModel() {

    // ========================================================================
    // STATE MANAGEMENT
    // ========================================================================

    /**
     * Private mutable state - only this ViewModel can modify it.
     * Using mutableStateOf integrates with Compose's state system.
     */
    private val _state = mutableStateOf(FavoritesState())

    /**
     * Public immutable state exposed to the UI layer.
     * FavoritesScreen observes this and recomposes when it changes.
     */
    val state: State<FavoritesState> = _state

    // ========================================================================
    // INITIALIZATION
    // ========================================================================

    /**
     * Init block runs when the ViewModel is first created.
     * We start observing favorite articles immediately.
     */
    init {
        getFavoriteArticles()
    }

    // ========================================================================
    // DATA LOADING
    // ========================================================================

    /**
     * Starts observing the Flow of favorite articles from the database.
     *
     * FLOW OPERATORS EXPLAINED:
     *
     * 1. getFavoriteArticlesUseCase() - Returns Flow<List<Article>>
     *    This Flow emits a new list every time the favorites table changes
     *
     * 2. .onEach { articles -> ... } - Side effect operator
     *    Called for each emission, we update our state here
     *
     * 3. .launchIn(viewModelScope) - Starts collection in ViewModel's scope
     *    The collection is automatically cancelled when ViewModel is destroyed
     *
     * WHY USE FLOW INSTEAD OF ONE-TIME FETCH?
     * - One-time fetch: Would require manual refresh when data changes
     * - Flow: Automatically receives updates when database changes
     * - Result: DetailScreen adds/removes favorite → This list updates automatically
     */
    private fun getFavoriteArticles() {
        getFavoriteArticlesUseCase()
            .onEach { articles ->
                // Log for debugging - helpful during development
                Log.d(TAG, "Favorite articles received: ${articles.size} articles")
                articles.forEachIndexed { index, article ->
                    Log.d(TAG, "  [$index] Title: ${article.title}, isFavorite: ${article.isFavorite}")
                }

                // Update the UI state with the new list
                // Using copy() on a data class creates a new immutable instance
                _state.value = FavoritesState(articles = articles)
            }
            .launchIn(viewModelScope)  // Collect in ViewModel scope (auto-cancelled on destroy)
    }
}
