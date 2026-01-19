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

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.LoginScreen.route
    ) {
        composable(route = Screen.LoginScreen.route) {
            LoginScreen(navController = navController)
        }

        composable(route = Screen.HomeScreen.route) {
            HomeScreen(
                onArticleClick = { article ->
                    navController.currentBackStackEntry?.savedStateHandle?.set("article", article)
                    navController.navigate(Screen.DetailScreen.route)
                },
                navController = navController
            )
        }

        composable(route = Screen.DetailScreen.route) {
            val article = navController.previousBackStackEntry?.savedStateHandle?.get<Article>("article")
            DetailScreen(
                article = article,
                navController = navController
            )
        }

        composable(route = Screen.FavoritesScreen.route) {
            FavoritesScreen(navController = navController)
        }
    }
}
