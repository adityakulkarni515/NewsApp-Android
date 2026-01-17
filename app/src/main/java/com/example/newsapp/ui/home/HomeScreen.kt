package com.example.newsapp.ui.home

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.newsapp.domain.model.Article
import com.example.newsapp.ui.home.components.ArticleItem
import com.example.newsapp.ui.home.components.ShimmerLoading
import com.example.newsapp.ui.navigation.Screen
import com.example.newsapp.viewmodel.HomeViewModel

/**
 * Premium Home Screen displaying top news headlines.
 *
 * Features:
 * - Gradient header with app branding
 * - Premium article cards with hero images
 * - Smooth loading animations
 * - Floating action button style favorites access
 *
 * This screen fetches news from the API and displays them in a beautifully
 * designed list with proper spacing between items.
 *
 * @param onArticleClick Callback when user taps an article (handles navigation).
 * @param navController Navigation controller for navigating to other screens.
 * @param viewModel The ViewModel that provides the news data (injected by Hilt).
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onArticleClick: (Article) -> Unit,
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    // Collect state from ViewModel
    val state by viewModel.state

    // Define gradient colors for the header
    val headerGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF6366f1),
            Color(0xFF8b5cf6),
            Color(0xFFa855f7)
        )
    )

    Scaffold(
        topBar = {
            // Custom gradient top app bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.Transparent
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(headerGradient)
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // App Branding
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Logo Icon
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = "App Logo",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = "Top Headlines",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Stay updated with latest news",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }

                        // Favorites Button
                        FilledIconButton(
                            onClick = {
                                navController.navigate(Screen.FavoritesScreen.route)
                            },
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = Color.White.copy(alpha = 0.2f)
                            ),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Favorite,
                                contentDescription = "View Favorites",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            when {
                // Loading State - Show shimmer animation
                state.isLoading -> {
                    ShimmerLoading()
                }

                // Error State - Show error message
                state.error != null -> {
                    ErrorContent(
                        error = state.error!!,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Success State - Show article list
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            top = 16.dp,
                            bottom = 24.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = state.articles,
                            key = { article -> article.url }
                        ) { article ->
                            ArticleItem(
                                article = article,
                                onItemClick = onArticleClick
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Error content displayed when news fetching fails.
 *
 * Shows a friendly error message with an icon.
 *
 * @param error The error message to display.
 * @param modifier Modifier for styling.
 */
@Composable
private fun ErrorContent(
    error: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .padding(32.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Oops!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                )
            }
        }
    }
}
