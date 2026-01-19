package com.example.newsapp.ui.navigation

sealed class Screen(val route: String) {
    object LoginScreen : Screen("login_screen")
    object HomeScreen : Screen("home_screen")
    object DetailScreen : Screen("detail_screen")
    object FavoritesScreen : Screen("favorites_screen")
}
