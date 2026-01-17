package com.example.newsapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * This is the main Application class for the News app.
 * The @HiltAndroidApp annotation enables Hilt for the entire application,
 * setting up the dependency injection container.
 */
@HiltAndroidApp
class NewsApp : Application()
