package com.example.newsapp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.newsapp.data.db.dao.ArticleDao
import com.example.newsapp.data.db.entity.ArticleEntity

/**
 * The main Room database class for the application.
 * This abstract class extends RoomDatabase and defines the database configuration.
 * @property version The version of the database schema.
 * @property entities A list of all entity classes that are part of this database.
 */
@Database(
    entities = [ArticleEntity::class], // Specifies the tables in the database.
    version = 1, // The database schema version.
    exportSchema = false // Disables exporting the schema to a file.
)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Provides an abstract method to get the Data Access Object (DAO) for articles.
     * Room will generate the implementation for this method.
     * @return An instance of ArticleDao.
     */
    abstract fun articleDao(): ArticleDao
}
