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

data class FavoritesState(
    val articles: List<Article> = emptyList()
)

private const val TAG = "FavoritesViewModel"

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getFavoriteArticlesUseCase: GetFavoriteArticlesUseCase
) : ViewModel() {

    private val _state = mutableStateOf(FavoritesState())
    val state: State<FavoritesState> = _state

    init {
        getFavoriteArticles()
    }

    private fun getFavoriteArticles() {
        getFavoriteArticlesUseCase()
            .onEach { articles ->
                Log.d(TAG, "Favorite articles received: ${articles.size} articles")
                articles.forEachIndexed { index, article ->
                    Log.d(TAG, "  [$index] Title: ${article.title}, isFavorite: ${article.isFavorite}")
                }
                _state.value = FavoritesState(articles = articles)
            }
            .launchIn(viewModelScope)
    }
}
