package com.example.newsapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.newsapp.domain.model.Article
import com.example.newsapp.ui.login.LoginScreen
import com.example.newsapp.ui.home.HomeScreen
import com.example.newsapp.ui.detail.DetailScreen
import com.example.newsapp.ui.favorites.FavoritesScreen

/**
 * Defines the navigation graph for the entire application.
 *
 * WHAT IS A NAVIGATION GRAPH?
 * A navigation graph is a map of all the screens in your app and how they connect.
 * Think of it like a flowchart showing which screens can navigate to which other screens.
 *
 * HOW JETPACK COMPOSE NAVIGATION WORKS:
 * 1. NavHost - A container that displays one screen at a time
 * 2. NavController - Manages navigation between screens (back stack, navigation actions)
 * 3. composable() - Defines each screen and its route (URL-like path)
 * 4. SavedStateHandle - Used to pass data between screens
 *
 * NAVIGATION FLOW IN THIS APP:
 * LoginScreen → HomeScreen → DetailScreen
 *                    ↓
 *            FavoritesScreen → DetailScreen
 *
 * @param navController The controller that manages navigation within the graph.
 */
@Composable
fun NavGraph(navController: NavHostController) {
    // NavHost is a container that displays the current navigation destination
    // It maintains a back stack of screens the user has visited
    NavHost(
        navController = navController,
        // The first screen shown when the app starts
        startDestination = Screen.LoginScreen.route
    ) {
        // ====================================================================
        // LOGIN SCREEN
        // ====================================================================
        // Route: "login_screen"
        // This is the entry point of the app where users log in
        composable(route = Screen.LoginScreen.route) {
            LoginScreen(navController = navController)
        }

        // ====================================================================
        // HOME SCREEN
        // ====================================================================
        // Route: "home_screen"
        // Displays the list of news articles from the API
        composable(route = Screen.HomeScreen.route) {
            HomeScreen(
                // Callback when user taps on an article
                onArticleClick = { article ->
                    // HOW WE PASS DATA BETWEEN SCREENS:
                    // We store the Article object in the current backStackEntry's savedStateHandle
                    // This is like putting data in a temporary storage that the next screen can access
                    navController.currentBackStackEntry?.savedStateHandle?.set("article", article)
                    // Navigate to the detail screen
                    navController.navigate(Screen.DetailScreen.route)
                },
                navController = navController
            )
        }

        // ====================================================================
        // DETAIL SCREEN
        // ====================================================================
        // Route: "detail_screen"
        // Displays the full content of a single article
        // Includes the favorite button (heart icon)
        composable(route = Screen.DetailScreen.route) {
            // RETRIEVING DATA FROM PREVIOUS SCREEN:
            // When we navigate here, the previous screen stored the article in savedStateHandle
            // We retrieve it from the previousBackStackEntry (the screen we came from)
            val article = navController.previousBackStackEntry?.savedStateHandle?.get<Article>("article")

            // Pass the article to DetailScreen
            // The screen will handle null case by navigating back
            DetailScreen(
                article = article,
                navController = navController
            )
        }

        // ====================================================================
        // FAVORITES SCREEN
        // ====================================================================
        // Route: "favorites_screen"
        // Displays the list of articles the user has saved as favorites
        composable(route = Screen.FavoritesScreen.route) {
            FavoritesScreen(navController = navController)
        }
    }
}
