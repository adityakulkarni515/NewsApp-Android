package com.example.newsapp.ui.navigation

/**
 * Sealed class defining all navigation routes in the application.
 *
 * WHAT IS A SEALED CLASS?
 * A sealed class is a special Kotlin class that:
 * - Has a fixed set of subclasses (defined here)
 * - Cannot be extended outside this file
 * - Works well with 'when' expressions (compiler ensures all cases handled)
 *
 * WHY USE SEALED CLASS FOR NAVIGATION?
 * 1. Type Safety: Routes are objects, not just strings (fewer typos)
 * 2. Discoverability: All screens are in one place
 * 3. Compile-time checks: IDE helps find route usage
 * 4. Easy refactoring: Change route in one place, affects everywhere
 *
 * ALTERNATIVE APPROACHES:
 * - String constants: Less type-safe, easy to typo
 * - Enum: Can't have different parameters per route
 * - Data class: Good for routes with parameters
 *
 * NAVIGATION FLOW:
 * ┌────────────────┐
 * │  LoginScreen   │ (Start destination)
 * └───────┬────────┘
 *         │ (after login)
 *         ▼
 * ┌────────────────┐
 * │   HomeScreen   │◄─────────────────┐
 * └───────┬────────┘                  │
 *         │                           │
 *    ┌────┴────┐                      │
 *    ▼         ▼                      │
 * ┌──────┐ ┌──────────────┐          │
 * │Detail│ │FavoritesScreen│         │
 * │Screen│ └───────┬───────┘         │
 * └──────┘         │                  │
 *                  ▼                  │
 *           ┌────────────┐           │
 *           │DetailScreen│───────────┘
 *           └────────────┘  (back navigation)
 *
 * @property route The string path used by Jetpack Navigation to identify this destination.
 */
sealed class Screen(val route: String) {
    /**
     * Login screen - The entry point of the app.
     * Users must log in before accessing other screens.
     * Route: "login_screen"
     */
    object LoginScreen : Screen("login_screen")

    /**
     * Home screen - Displays the list of top news headlines.
     * This is the main screen after login.
     * Route: "home_screen"
     */
    object HomeScreen : Screen("home_screen")

    /**
     * Detail screen - Shows full article content with favorite button.
     * Accessible from HomeScreen or FavoritesScreen.
     * Route: "detail_screen"
     */
    object DetailScreen : Screen("detail_screen")

    /**
     * Favorites screen - Shows list of favorited articles.
     * Accessible from HomeScreen via the heart icon in app bar.
     * Route: "favorites_screen"
     */
    object FavoritesScreen : Screen("favorites_screen")
}
