package com.example.newsapp.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.example.newsapp.domain.model.Article
import com.example.newsapp.domain.usecase.CheckFavoriteStatusUseCase
import com.example.newsapp.domain.usecase.DeleteArticleUseCase
import com.example.newsapp.domain.usecase.SaveArticleUseCase
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class DetailViewModelTest {

    private lateinit var detailViewModel: DetailViewModel
    private val saveArticleUseCase = mock<SaveArticleUseCase>()
    private val deleteArticleUseCase = mock<DeleteArticleUseCase>()
    private val checkFavoriteStatusUseCase = mock<CheckFavoriteStatusUseCase>()
    private val testDispatcher = StandardTestDispatcher()

    private val testArticle = Article(
        author = "Author",
        content = "Content",
        description = "Description",
        publishedAt = "2023-01-01T00:00:00Z",
        sourceName = "Source",
        title = "Title",
        url = "http://example.com/article",
        urlToImage = "http://example.com/image",
        isFavorite = false
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun setupViewModel(article: Article, isFavorite: Boolean) = runTest {
        val savedStateHandle = SavedStateHandle(mapOf("article" to article))
        whenever(checkFavoriteStatusUseCase.invoke(article.url)).thenReturn(MutableStateFlow(isFavorite))

        detailViewModel = DetailViewModel(
            savedStateHandle,
            saveArticleUseCase,
            deleteArticleUseCase,
            checkFavoriteStatusUseCase
        )
        // Initialize the article in the ViewModel
        detailViewModel.initArticle(article)
        // Advance the dispatcher to ensure the init block's checkFavoriteStatus completes
        advanceUntilIdle()
    }

    @Test
    fun `onFavoriteClick with non-favorite article should save article and set isFavorite to true`() = runTest {
        setupViewModel(testArticle, isFavorite = false)

        detailViewModel.onFavoriteClick()
        advanceUntilIdle()

        verify(saveArticleUseCase).invoke(testArticle.copy(isFavorite = true))
        assertThat(detailViewModel.state.value.isFavorite).isTrue()
    }

    @Test
    fun `onFavoriteClick with favorite article should delete article and set isFavorite to false`() = runTest {
        setupViewModel(testArticle.copy(isFavorite = true), isFavorite = true)

        detailViewModel.onFavoriteClick()
        advanceUntilIdle()

        verify(deleteArticleUseCase).invoke(testArticle.copy(isFavorite = false))
        assertThat(detailViewModel.state.value.isFavorite).isFalse()
    }

    @Test
    fun `checkFavoriteStatus should update isFavorite state correctly on initial load`() = runTest {
        setupViewModel(testArticle, isFavorite = true)
        assertThat(detailViewModel.state.value.isFavorite).isTrue()

        // Test with false
        setupViewModel(testArticle, isFavorite = false)
        assertThat(detailViewModel.state.value.isFavorite).isFalse()
    }

    @Test
    fun `article StateFlow should contain the article from SavedStateHandle`() = runTest {
        setupViewModel(testArticle, isFavorite = false)
        assertThat(detailViewModel.article.first()).isEqualTo(testArticle)
    }

    @Test
    fun `onFavoriteClick should optimistically update isFavorite state`() = runTest {
        setupViewModel(testArticle, isFavorite = false)

        // Before click, isFavorite is false
        assertThat(detailViewModel.state.value.isFavorite).isFalse()

        detailViewModel.onFavoriteClick()

        // Immediately after click, but before use case completes, isFavorite should be true
        assertThat(detailViewModel.state.value.isFavorite).isTrue()
    }

    @Test
    fun `checkFavoriteStatus updates state when flow emits new value`() = runTest {
        val initialIsFavorite = false
        val updatedIsFavorite = true
        val favoriteStatusFlow = MutableStateFlow(initialIsFavorite)

        val savedStateHandle = SavedStateHandle(mapOf("article" to testArticle))
        whenever(checkFavoriteStatusUseCase.invoke(testArticle.url)).thenReturn(favoriteStatusFlow)

        detailViewModel = DetailViewModel(
            savedStateHandle,
            saveArticleUseCase,
            deleteArticleUseCase,
            checkFavoriteStatusUseCase
        )
        // Initialize the article in the ViewModel
        detailViewModel.initArticle(testArticle)
        advanceUntilIdle() // Process initial flow emission

        assertThat(detailViewModel.state.value.isFavorite).isEqualTo(initialIsFavorite)

        // Emit a new value from the flow
        favoriteStatusFlow.value = updatedIsFavorite
        advanceUntilIdle() // Process new flow emission

        assertThat(detailViewModel.state.value.isFavorite).isEqualTo(updatedIsFavorite)
    }
}
