package com.example.newsapp.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.domain.model.Article
import com.example.newsapp.domain.usecase.CheckFavoriteStatusUseCase
import com.example.newsapp.domain.usecase.DeleteArticleUseCase
import com.example.newsapp.domain.usecase.SaveArticleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Represents the state for the Detail screen.
 *
 * This data class holds the UI state that the DetailScreen observes.
 * Using a data class allows for easy immutable updates via copy().
 *
 * @property isFavorite Indicates whether the currently displayed article is a favorite.
 *                      This is used to toggle the heart icon appearance (filled/unfilled).
 */
data class DetailState(
    val isFavorite: Boolean = false
)

private const val TAG = "DetailViewModel"

/**
 * ViewModel for the News Detail screen.
 *
 * This ViewModel manages the state of a single article, including its favorite status.
 * It follows the MVVM (Model-View-ViewModel) pattern where:
 * - Model: The Article domain object and use cases
 * - View: DetailScreen composable
 * - ViewModel: This class, which acts as a bridge between them
 *
 * Key responsibilities:
 * 1. Hold the current article being displayed
 * 2. Track and update the favorite status in real-time
 * 3. Handle favorite toggle operations (save/delete from database)
 * 4. Provide optimistic UI updates for better user experience
 *
 * @param savedStateHandle A handle to saved state (kept for potential future use with nav args).
 * @param saveArticleUseCase The use case for saving an article as a favorite to the database.
 * @param deleteArticleUseCase The use case for removing an article from favorites.
 * @param checkFavoriteStatusUseCase The use case for observing if an article is favorited.
 */
@HiltViewModel
class DetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val saveArticleUseCase: SaveArticleUseCase,
    private val deleteArticleUseCase: DeleteArticleUseCase,
    private val checkFavoriteStatusUseCase: CheckFavoriteStatusUseCase
) : ViewModel() {

    // ============================================================================
    // STATE MANAGEMENT
    // ============================================================================

    /**
     * Private mutable state for the Detail screen UI elements.
     * We use mutableStateOf for Compose integration - this allows the UI to
     * automatically recompose when the state changes.
     */
    private val _state = mutableStateOf(DetailState())

    /**
     * Public immutable State exposed to the UI layer.
     * The UI observes this state and recomposes when it changes.
     * Making it immutable prevents the UI from accidentally modifying the state directly.
     */
    val state: State<DetailState> = _state

    /**
     * Private MutableStateFlow to hold the current Article object.
     * StateFlow is used here because:
     * 1. It always has a current value (unlike SharedFlow)
     * 2. It only emits when the value actually changes (equality-based)
     * 3. It integrates well with Compose via collectAsState()
     */
    private val _article = MutableStateFlow<Article?>(null)

    /**
     * Public immutable StateFlow for the Article, observed by the UI.
     */
    val article: StateFlow<Article?> = _article.asStateFlow()

    /**
     * Job reference for the favorite status observer.
     * We keep this reference so we can cancel it when initializing a new article,
     * preventing memory leaks and ensuring we observe the correct article.
     */
    private var favoriteStatusJob: Job? = null

    /**
     * Flag to track if the article has been initialized.
     * This prevents re-initialization when the screen recomposes.
     */
    private var isInitialized = false

    // ============================================================================
    // INITIALIZATION
    // ============================================================================

    /**
     * Initializes the ViewModel with the article passed from navigation.
     *
     * WHY THIS METHOD EXISTS:
     * In Jetpack Compose Navigation, when using savedStateHandle in a ViewModel,
     * it expects navigation arguments to be passed via the route definition.
     * However, we're passing the Article object through the backStackEntry's savedStateHandle,
     * which is a DIFFERENT savedStateHandle than the ViewModel's!
     *
     * So we need this method to receive the article from the DetailScreen composable
     * and properly initialize the ViewModel with it.
     *
     * @param article The Article object passed from navigation via the composable.
     */
    fun initArticle(article: Article) {
        // Prevent re-initialization on recomposition
        // This is important because Compose may recompose the screen multiple times
        if (isInitialized) {
            Log.d(TAG, "Article already initialized, skipping re-initialization")
            return
        }

        isInitialized = true
        Log.d(TAG, "Initializing article: ${article.title}, URL: ${article.url}")

        // Cancel any existing observer to prevent memory leaks
        favoriteStatusJob?.cancel()

        // Start observing the favorite status from the database
        // This creates a reactive connection to the Room database
        favoriteStatusJob = checkFavoriteStatusUseCase(article.url)
            .onEach { isFavorite ->
                Log.d(TAG, "Favorite status for ${article.title} changed to: $isFavorite")
                // Update the article with the current favorite status from DB
                _article.value = article.copy(isFavorite = isFavorite)
                // Update the UI state to reflect the favorite status
                _state.value = _state.value.copy(isFavorite = isFavorite)
            }
            .launchIn(viewModelScope)
    }

    // ============================================================================
    // USER ACTIONS
    // ============================================================================

    /**
     * Handles the favorite button click event.
     *
     * This method implements the "Optimistic UI Update" pattern:
     * 1. First, immediately update the UI to show the new state (user sees instant feedback)
     * 2. Then, perform the actual database operation in the background
     * 3. If the database operation fails, revert the UI to the previous state
     *
     * WHY OPTIMISTIC UPDATES?
     * - Users expect instant feedback when they tap a button
     * - Database operations can take time (milliseconds to seconds)
     * - Without optimistic updates, there would be a noticeable delay
     *
     * The method toggles between:
     * - Not favorite -> Favorite: Shows filled heart, saves article to Room database
     * - Favorite -> Not favorite: Shows empty heart, removes article from Room database
     */
    fun onFavoriteClick() {
        // Get the current article from our StateFlow
        // If null, we can't proceed (shouldn't happen if UI is correct)
        _article.value?.let { currentArticle ->
            Log.d(TAG, "onFavoriteClick called for article: ${currentArticle.title}, current isFavorite: ${_state.value.isFavorite}")

            // Toggle the favorite status (true becomes false, false becomes true)
            val newFavoriteStatus = !_state.value.isFavorite

            // Create a new Article object with the updated favorite status
            // We use copy() because data classes are immutable by design
            val articleToProcess = currentArticle.copy(isFavorite = newFavoriteStatus)

            // OPTIMISTIC UPDATE STEP 1: Update the UI immediately
            // This gives the user instant visual feedback
            _article.value = articleToProcess
            _state.value = _state.value.copy(isFavorite = newFavoriteStatus)
            Log.d(TAG, "Optimistically updated isFavorite to: $newFavoriteStatus for ${currentArticle.title}")

            // OPTIMISTIC UPDATE STEP 2: Perform database operation in background
            // viewModelScope ensures the coroutine is cancelled if the ViewModel is destroyed
            viewModelScope.launch {
                try {
                    if (newFavoriteStatus) {
                        // User wants to ADD this article to favorites
                        // Call the use case which will save to Room database
                        saveArticleUseCase(articleToProcess)
                        Log.d(TAG, "SaveArticleUseCase executed for: ${currentArticle.title}")
                    } else {
                        // User wants to REMOVE this article from favorites
                        // Call the use case which will delete from Room database
                        deleteArticleUseCase(articleToProcess)
                        Log.d(TAG, "DeleteArticleUseCase executed for: ${currentArticle.title}")
                    }
                    Log.d(TAG, "Database operation successful for: ${currentArticle.title}")
                } catch (e: Exception) {
                    // OPTIMISTIC UPDATE STEP 3: Revert if operation fails
                    Log.e(TAG, "Database operation failed for ${currentArticle.title}: ${e.message}")
                    // Restore the previous state since the operation failed
                    _state.value = _state.value.copy(isFavorite = !newFavoriteStatus)
                    _article.value = currentArticle
                }
                // NOTE: The checkFavoriteStatus Flow observer will also update the state
                // when the database changes, providing an extra layer of consistency
            }
        } ?: run {
            // This should never happen if the UI is set up correctly
            Log.e(TAG, "onFavoriteClick: No article available to process.")
        }
    }

    // ============================================================================
    // LIFECYCLE
    // ============================================================================

    /**
     * Called when the ViewModel is about to be destroyed.
     * Clean up any resources here.
     */
    override fun onCleared() {
        super.onCleared()
        // Cancel the favorite status observer job
        favoriteStatusJob?.cancel()
        Log.d(TAG, "DetailViewModel cleared")
    }
}
