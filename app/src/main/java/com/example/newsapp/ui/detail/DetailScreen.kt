package com.example.newsapp.ui.detail

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.newsapp.domain.model.Article
import com.example.newsapp.viewmodel.DetailViewModel

/**
 * Composable function for the News Detail Screen.
 *
 * This screen displays the full content of a single news article including:
 * - Article image at the top
 * - Title, author, and publication date
 * - Full article content
 * - A favorite button (heart icon) in the top app bar
 *
 * HOW NAVIGATION WORKS:
 * 1. The user taps an article in HomeScreen or FavoritesScreen
 * 2. The article object is stored in the navigation backStackEntry's savedStateHandle
 * 3. NavGraph retrieves this article and passes it to this composable
 * 4. We initialize the ViewModel with this article using LaunchedEffect
 *
 * WHY LAUNCHEDEFFECT?
 * LaunchedEffect is used to perform side effects in Compose. Here we use it to:
 * - Initialize the ViewModel with the article when the screen first loads
 * - The key (article.url) ensures this only runs once per article
 *
 * @param article The article to be displayed, passed from navigation.
 * @param navController The navigation controller for handling back navigation.
 * @param viewModel The ViewModel that manages this screen's state (provided by Hilt).
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    article: Article?,
    navController: NavController,
    viewModel: DetailViewModel = hiltViewModel()
) {
    // ========================================================================
    // STEP 1: Initialize the ViewModel with the article
    // ========================================================================
    // LaunchedEffect runs when the composable enters composition
    // Using article?.url as the key ensures it only runs once per article
    // This is the bridge between navigation (which provides the article) and
    // the ViewModel (which manages the state)
    LaunchedEffect(key1 = article?.url) {
        article?.let {
            // Tell the ViewModel which article we're displaying
            // The ViewModel will then start observing the favorite status from the database
            viewModel.initArticle(it)
        }
    }

    // ========================================================================
    // STEP 2: Collect state from ViewModel
    // ========================================================================
    // Collect the UI state which contains the favorite status
    // This will automatically recompose the UI when the state changes
    val uiState by viewModel.state

    // Collect the Article from ViewModel's StateFlow
    // This gives us the article with the most up-to-date favorite status
    val currentArticle by viewModel.article.collectAsState()

    // Use ViewModel's article if available (has correct favorite status),
    // otherwise fall back to the navigation-provided article
    val displayArticle = currentArticle ?: article

    // ========================================================================
    // STEP 3: Handle null article case
    // ========================================================================
    // If somehow no article is available, navigate back
    // This is a safety check - shouldn't happen with correct navigation setup
    if (displayArticle == null) {
        navController.navigateUp()
        return
    }

    // ========================================================================
    // STEP 4: Build the UI
    // ========================================================================
    // Scaffold provides the basic Material Design 3 layout structure
    // It includes slots for top bar, bottom bar, floating action button, etc.
    Scaffold(
        topBar = {
            // TopAppBar displays the title and action buttons at the top
            TopAppBar(
                // Display the news source name as the title
                title = { Text(displayArticle.sourceName ?: "Detail") },

                // Navigation icon (back arrow) on the left side
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },

                // Action buttons on the right side of the app bar
                actions = {
                    // FAVORITE BUTTON - The heart icon
                    // This is the main feature we're implementing
                    IconButton(onClick = { viewModel.onFavoriteClick() }) {
                        Icon(
                            // Choose icon based on favorite state:
                            // - Filled heart (Icons.Filled.Favorite) when favorited
                            // - Outlined heart (Icons.Filled.FavoriteBorder) when not favorited
                            imageVector = if (uiState.isFavorite) {
                                Icons.Filled.Favorite
                            } else {
                                Icons.Filled.FavoriteBorder
                            },
                            contentDescription = if (uiState.isFavorite) {
                                "Remove from favorites"
                            } else {
                                "Add to favorites"
                            },
                            // Use red color for filled heart to make it more visible
                            tint = if (uiState.isFavorite) {
                                Color.Red
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        // Main content area - A scrollable Column
        // We use verticalScroll to allow scrolling when content is longer than screen
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)  // Apply padding from Scaffold (avoids overlap with app bar)
                .verticalScroll(rememberScrollState())  // Make the column scrollable
        ) {
            // ================================================================
            // Article Image
            // ================================================================
            // AsyncImage from Coil library loads images asynchronously
            // This prevents UI blocking while the image downloads
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(displayArticle.urlToImage)  // The URL of the image
                    .crossfade(true)  // Smooth fade-in animation when image loads
                    .build(),
                contentDescription = "Article Image",
                contentScale = ContentScale.Crop,  // Crop image to fill the space
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            )

            // ================================================================
            // Article Text Content
            // ================================================================
            Column(modifier = Modifier.padding(16.dp)) {
                // Article Title - Large, prominent text
                Text(
                    text = displayArticle.title ?: "No Title",
                    style = MaterialTheme.typography.headlineSmall
                )

                // Vertical spacing between elements
                Spacer(modifier = Modifier.height(8.dp))

                // Author and Date - Smaller, secondary text
                Text(
                    text = "By ${displayArticle.author ?: "Unknown"} | ${displayArticle.publishedAt ?: "No Date"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Full Article Content - Main body text
                Text(
                    text = displayArticle.content ?: "No Content",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
