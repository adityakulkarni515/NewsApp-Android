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

data class HomeState(
    val isLoading: Boolean = false,
    val articles: List<Article> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTopHeadlinesUseCase: GetTopHeadlinesUseCase
) : ViewModel() {

    private val _state = mutableStateOf(HomeState())
    val state: State<HomeState> = _state

    init {
        getNews()
    }

    private fun getNews() {
        viewModelScope.launch {
            _state.value = HomeState(isLoading = true)

            getTopHeadlinesUseCase(page = 1, pageSize = 20)
                .onSuccess { articles ->
                    _state.value = HomeState(articles = articles)
                }
                .onFailure { throwable ->
                    _state.value = HomeState(
                        error = throwable.localizedMessage ?: "An unknown error occurred"
                    )
                }
        }
    }
}
