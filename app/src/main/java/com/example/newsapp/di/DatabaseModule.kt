package com.example.newsapp.di

import android.content.Context
import androidx.room.Room
import com.example.newsapp.data.db.AppDatabase
import com.example.newsapp.data.db.dao.ArticleDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * This Hilt module is responsible for providing database-related dependencies,
 * such as the Room database instance and Data Access Objects (DAOs).
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Provides a singleton instance of the AppDatabase.
     * Room's database builder is used to create the database instance.
     * @param context The application context, provided by Hilt.
     * @return A singleton instance of the AppDatabase.
     */
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        // Creates and configures the Room database.
        return Room.databaseBuilder(
            context, // The application context.
            AppDatabase::class.java, // The database class.
            "news_app_db" // The name of the database file.
        ).build() // Builds and returns the database instance.
    }

    /**
     * Provides a singleton instance of the ArticleDao.
     * The DAO is obtained from the AppDatabase instance.
     * @param database The AppDatabase instance provided by this module.
     * @return A singleton instance of the ArticleDao.
     */
    @Provides
    @Singleton
    fun provideArticleDao(database: AppDatabase): ArticleDao {
        // Returns the ArticleDao from the database instance.
        return database.articleDao()
    }
}
