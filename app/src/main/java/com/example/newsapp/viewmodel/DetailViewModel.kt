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

data class DetailState(
    val isFavorite: Boolean = false
)

private const val TAG = "DetailViewModel"

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val saveArticleUseCase: SaveArticleUseCase,
    private val deleteArticleUseCase: DeleteArticleUseCase,
    private val checkFavoriteStatusUseCase: CheckFavoriteStatusUseCase
) : ViewModel() {

    private val _state = mutableStateOf(DetailState())
    val state: State<DetailState> = _state

    private val _article = MutableStateFlow<Article?>(null)
    val article: StateFlow<Article?> = _article.asStateFlow()

    private var favoriteStatusJob: Job? = null
    private var isInitialized = false

    fun initArticle(article: Article) {
        if (isInitialized) {
            Log.d(TAG, "Article already initialized, skipping re-initialization")
            return
        }

        isInitialized = true
        Log.d(TAG, "Initializing article: ${article.title}, URL: ${article.url}")

        favoriteStatusJob?.cancel()

        favoriteStatusJob = checkFavoriteStatusUseCase(article.url)
            .onEach { isFavorite ->
                Log.d(TAG, "Favorite status for ${article.title} changed to: $isFavorite")
                _article.value = article.copy(isFavorite = isFavorite)
                _state.value = _state.value.copy(isFavorite = isFavorite)
            }
            .launchIn(viewModelScope)
    }

    fun onFavoriteClick() {
        _article.value?.let { currentArticle ->
            Log.d(TAG, "onFavoriteClick called for article: ${currentArticle.title}, current isFavorite: ${_state.value.isFavorite}")

            val newFavoriteStatus = !_state.value.isFavorite
            val articleToProcess = currentArticle.copy(isFavorite = newFavoriteStatus)

            _article.value = articleToProcess
            _state.value = _state.value.copy(isFavorite = newFavoriteStatus)
            Log.d(TAG, "Optimistically updated isFavorite to: $newFavoriteStatus for ${currentArticle.title}")

            viewModelScope.launch {
                try {
                    if (newFavoriteStatus) {
                        saveArticleUseCase(articleToProcess)
                        Log.d(TAG, "SaveArticleUseCase executed for: ${currentArticle.title}")
                    } else {
                        deleteArticleUseCase(articleToProcess)
                        Log.d(TAG, "DeleteArticleUseCase executed for: ${currentArticle.title}")
                    }
                    Log.d(TAG, "Database operation successful for: ${currentArticle.title}")
                } catch (e: Exception) {
                    Log.e(TAG, "Database operation failed for ${currentArticle.title}: ${e.message}")
                    _state.value = _state.value.copy(isFavorite = !newFavoriteStatus)
                    _article.value = currentArticle
                }
            }
        } ?: run {
            Log.e(TAG, "onFavoriteClick: No article available to process.")
        }
    }

    override fun onCleared() {
        super.onCleared()
        favoriteStatusJob?.cancel()
        Log.d(TAG, "DetailViewModel cleared")
    }
}
