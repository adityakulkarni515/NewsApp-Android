package com.example.newsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.newsapp.ui.navigation.NavGraph
import com.example.newsapp.ui.theme.NewsAppTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * The main and only activity in the application.
 * It serves as the entry point and hosts all the Composable screens.
 * The @AndroidEntryPoint annotation is necessary for Hilt to inject dependencies.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Sets the content of the activity to be a Composable function.
        setContent {
            // Applies the custom theme to the entire application.
            NewsAppTheme {
                // A Surface container using the 'background' color from the theme.
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Creates a NavController to handle navigation events.
                    val navController = rememberNavController()
                    // Sets up the navigation graph for the app.
                    NavGraph(navController = navController)
                }
            }
        }
    }
}