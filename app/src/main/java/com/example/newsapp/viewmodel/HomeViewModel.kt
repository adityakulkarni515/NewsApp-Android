package com.example.newsapp.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.domain.model.Article
import com.example.newsapp.domain.usecase.GetTopHeadlinesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Represents the UI state of the Home screen.
 *
 * This data class follows the "sealed state" pattern where all possible
 * states of the Home screen are represented in a single object.
 *
 * THREE POSSIBLE STATES:
 * 1. Loading: isLoading = true, articles = empty, error = null
 * 2. Success: isLoading = false, articles = [list], error = null
 * 3. Error: isLoading = false, articles = empty, error = "message"
 *
 * WHY USE A DATA CLASS?
 * - Immutable: Cannot be modified after creation (thread-safe)
 * - copy() function: Easy to create new state with some fields changed
 * - equals()/hashCode(): Compose can efficiently detect changes
 *
 * @property isLoading True when fetching news from API, shows shimmer animation.
 * @property articles The list of news articles from the API.
 * @property error Error message if API call failed, null otherwise.
 */
data class HomeState(
    val isLoading: Boolean = false,
    val articles: List<Article> = emptyList(),
    val error: String? = null
)

/**
 * ViewModel for the Home screen (main news list).
 *
 * This ViewModel demonstrates the basic MVVM pattern:
 * 1. It fetches data from the domain layer (Use Case)
 * 2. It transforms the data into UI state
 * 3. It exposes the state for the UI to observe
 *
 * LIFECYCLE:
 * - Created when HomeScreen is first displayed
 * - Survives configuration changes (screen rotation)
 * - init{} block runs once when ViewModel is created
 * - Destroyed only when the user navigates away completely
 *
 * DEPENDENCY INJECTION:
 * - @HiltViewModel tells Hilt this is a ViewModel to inject
 * - @Inject constructor: Hilt will provide the GetTopHeadlinesUseCase
 * - No manual ViewModel creation needed (Hilt handles it)
 *
 * @param getTopHeadlinesUseCase The use case for fetching news from the API.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTopHeadlinesUseCase: GetTopHeadlinesUseCase
) : ViewModel() {

    // ========================================================================
    // STATE MANAGEMENT
    // ========================================================================

    /**
     * Private mutable state - only this ViewModel can modify it.
     * mutableStateOf integrates with Compose's recomposition system.
     */
    private val _state = mutableStateOf(HomeState())

    /**
     * Public immutable state for UI observation.
     * HomeScreen observes this and recomposes when it changes.
     */
    val state: State<HomeState> = _state

    // ========================================================================
    // INITIALIZATION
    // ========================================================================

    /**
     * Init block runs when ViewModel is first created.
     * We immediately start fetching news articles.
     */
    init {
        getNews()
    }

    // ========================================================================
    // DATA LOADING
    // ========================================================================

    /**
     * Fetches top news headlines from the News API.
     *
     * This demonstrates the coroutine-based async pattern:
     * 1. Launch coroutine in viewModelScope (auto-cancelled on ViewModel destroy)
     * 2. Update state to show loading
     * 3. Call the use case (which calls repository → API)
     * 4. Handle success or failure
     *
     * KOTLIN RESULT TYPE:
     * The use case returns Result<List<Article>> which is a Kotlin type that
     * represents either success (with data) or failure (with exception).
     * - onSuccess { articles -> } - Called if API succeeded
     * - onFailure { throwable -> } - Called if API failed
     */
    private fun getNews() {
        viewModelScope.launch {
            // STATE: Loading - Show shimmer animation in UI
            _state.value = HomeState(isLoading = true)

            // Call the use case to fetch news
            // This is a suspend function, so it won't block the main thread
            getTopHeadlinesUseCase(page = 1, pageSize = 20)
                .onSuccess { articles ->
                    // STATE: Success - Display the list of articles
                    _state.value = HomeState(articles = articles)
                }
                .onFailure { throwable ->
                    // STATE: Error - Show error message to user
                    _state.value = HomeState(
                        error = throwable.localizedMessage ?: "An unknown error occurred"
                    )
                }
        }
    }
}
