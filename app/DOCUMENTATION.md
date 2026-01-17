# News App - Complete Documentation

## Table of Contents
1. [Introduction](#introduction)
2. [Architecture Overview](#architecture-overview)
3. [Project Structure](#project-structure)
4. [Key Concepts Explained](#key-concepts-explained)
5. [Data Flow](#data-flow)
6. [Favorites Feature Deep Dive](#favorites-feature-deep-dive)
7. [Navigation System](#navigation-system)
8. [State Management](#state-management)
9. [Dependency Injection](#dependency-injection)
10. [How to Run the App](#how-to-run-the-app)
11. [Common Issues and Solutions](#common-issues-and-solutions)

---

## Introduction

This is an Android News application built using modern Android development practices. The app fetches news from an API and allows users to save articles as favorites.

### Features
- View top news headlines
- Read full article details
- Add/remove articles from favorites
- View saved favorite articles
- Persistent favorites (saved locally)

### Technologies Used
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM + Clean Architecture
- **Dependency Injection**: Hilt
- **Networking**: Retrofit + OkHttp
- **Local Database**: Room
- **Async Programming**: Kotlin Coroutines + Flow
- **Image Loading**: Coil

---

## Architecture Overview

This app follows **Clean Architecture** principles with three main layers:

```
┌─────────────────────────────────────────────────────────────┐
│                     PRESENTATION LAYER                       │
│  (UI - Composables, ViewModels)                              │
│  What user sees and interacts with                           │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                       DOMAIN LAYER                           │
│  (Use Cases, Domain Models, Repository Interfaces)          │
│  Business logic - what the app does                          │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                        DATA LAYER                            │
│  (Repository Impl, API Service, Database, DTOs, Entities)   │
│  Where and how data is stored/fetched                        │
└─────────────────────────────────────────────────────────────┘
```

### Why Clean Architecture?

1. **Separation of Concerns**: Each layer has a specific responsibility
2. **Testability**: Easy to unit test each layer in isolation
3. **Maintainability**: Changes in one layer don't affect others
4. **Scalability**: Easy to add new features
5. **Flexibility**: Can swap implementations (e.g., change API or database)

---

## Project Structure

```
com.example.newsapp/
├── data/                          # DATA LAYER
│   ├── api/
│   │   └── NewsApiService.kt      # Retrofit API interface
│   ├── db/
│   │   ├── AppDatabase.kt         # Room database setup
│   │   ├── dao/
│   │   │   └── ArticleDao.kt      # Database operations
│   │   └── entity/
│   │       └── ArticleEntity.kt   # Database table structure
│   ├── model/
│   │   └── NewsDtos.kt            # API response models
│   ├── repository/
│   │   └── NewsRepositoryImpl.kt  # Repository implementation
│   └── Mappers.kt                 # Conversion functions
│
├── domain/                        # DOMAIN LAYER
│   ├── model/
│   │   └── Article.kt             # Business model
│   ├── repository/
│   │   └── NewsRepository.kt      # Repository interface
│   └── usecase/
│       └── NewsUseCases.kt        # Business operations
│
├── ui/                            # PRESENTATION LAYER
│   ├── navigation/
│   │   ├── Screen.kt              # Navigation routes
│   │   └── NavGraph.kt            # Navigation setup
│   ├── home/
│   │   ├── HomeScreen.kt          # News list screen
│   │   └── components/
│   │       ├── ArticleItem.kt     # Article card component
│   │       └── Shimmer.kt         # Loading animation
│   ├── detail/
│   │   └── DetailScreen.kt        # Article detail screen
│   ├── favorites/
│   │   └── FavoritesScreen.kt     # Favorites list screen
│   ├── login/
│   │   └── LoginScreen.kt         # Login screen
│   └── theme/
│       └── Theme.kt               # App theme/colors
│
├── viewmodel/                     # VIEW MODELS
│   ├── HomeViewModel.kt           # Home screen state
│   ├── DetailViewModel.kt         # Detail screen state
│   ├── FavoritesViewModel.kt      # Favorites screen state
│   └── LoginViewModel.kt          # Login screen state
│
├── di/                            # DEPENDENCY INJECTION
│   ├── AppModule.kt               # App-level dependencies
│   ├── DatabaseModule.kt          # Database dependencies
│   └── NetworkModule.kt           # Network dependencies
│
├── MainActivity.kt                # App entry point
└── NewsApp.kt                     # Application class
```

---

## Key Concepts Explained

### 1. Jetpack Compose

Compose is Android's modern toolkit for building native UI. Instead of XML layouts, you write UI in Kotlin.

```kotlin
// Traditional Android (XML + View)
// layout.xml: <TextView android:text="Hello" />
// Activity: textView.text = "Hello"

// Jetpack Compose
@Composable
fun Greeting() {
    Text(text = "Hello")
}
```

**Key Compose Concepts:**
- **@Composable**: Marks a function as a UI component
- **State**: Data that, when changed, triggers UI update (recomposition)
- **Recomposition**: Compose's way of updating the UI when state changes

### 2. ViewModel

ViewModel holds and manages UI-related data. It survives configuration changes (like screen rotation).

```kotlin
class HomeViewModel : ViewModel() {
    // State survives rotation
    private val _state = mutableStateOf(HomeState())
    val state: State<HomeState> = _state

    fun loadNews() {
        // Load data
    }
}
```

### 3. Kotlin Flow

Flow is like a pipe that emits values over time. Unlike regular functions that return once, Flow can emit multiple values.

```kotlin
// Regular function - returns once
fun getArticle(): Article

// Flow - can emit multiple values over time
fun getArticles(): Flow<List<Article>>

// In Room, when database changes, Flow emits new data automatically
@Query("SELECT * FROM articles")
fun getAllArticles(): Flow<List<Article>>
```

### 4. Room Database

Room is a database library that makes SQLite easier to use.

```kotlin
// Entity = Table
@Entity(tableName = "articles")
data class ArticleEntity(
    @PrimaryKey val url: String,
    val title: String
)

// DAO = Database operations
@Dao
interface ArticleDao {
    @Insert
    suspend fun insert(article: ArticleEntity)

    @Query("SELECT * FROM articles")
    fun getAll(): Flow<List<ArticleEntity>>
}
```

### 5. Coroutines

Coroutines allow you to write asynchronous code that looks synchronous.

```kotlin
// Without coroutines (callback hell)
api.getNews(object : Callback {
    override fun onSuccess(news: List<News>) {
        database.save(news, object : Callback {
            override fun onSuccess() {
                // Update UI
            }
        })
    }
})

// With coroutines (clean and readable)
viewModelScope.launch {
    val news = api.getNews()  // Suspends, doesn't block
    database.save(news)       // Suspends, doesn't block
    // Update UI
}
```

### 6. Hilt (Dependency Injection)

Hilt automatically provides dependencies where needed, reducing boilerplate.

```kotlin
// Without DI - you create everything manually
class HomeViewModel {
    private val repository = NewsRepositoryImpl(
        NewsApiService.create(),
        AppDatabase.create().articleDao()
    )
}

// With Hilt - dependencies are injected automatically
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: NewsRepository  // Hilt provides this
) : ViewModel()
```

---

## Data Flow

### Fetching News from API

```
User opens HomeScreen
        │
        ▼
HomeScreen created, observes HomeViewModel.state
        │
        ▼
HomeViewModel.init() calls getNews()
        │
        ▼
GetTopHeadlinesUseCase is called
        │
        ▼
NewsRepositoryImpl.getTopHeadlines()
        │
        ▼
NewsApiService makes HTTP request to News API
        │
        ▼
API returns JSON response
        │
        ▼
Retrofit converts JSON to NewsResponse (with ArticleDto list)
        │
        ▼
Repository maps ArticleDto → Article (domain model)
        │
        ▼
Use case returns Result<List<Article>>
        │
        ▼
ViewModel updates _state with articles
        │
        ▼
Compose recomposes HomeScreen with new articles
        │
        ▼
User sees list of news articles
```

### Saving a Favorite

```
User taps heart icon on DetailScreen
        │
        ▼
DetailViewModel.onFavoriteClick() called
        │
        ▼
Optimistically update UI (heart fills immediately)
        │
        ▼
Launch coroutine
        │
        ▼
SaveArticleUseCase(article) called
        │
        ▼
NewsRepositoryImpl.saveArticle()
        │
        ▼
ArticleDao.insertArticle() - Saves to SQLite
        │
        ▼
Room notifies all observers that data changed
        │
        ▼
CheckFavoriteStatusUseCase Flow emits: isFavorite = true
        │
        ▼
GetFavoriteArticlesUseCase Flow emits: updated list
        │
        ▼
FavoritesScreen automatically updates (when navigated to)
```

---

## Favorites Feature Deep Dive

The favorites feature is the core functionality you asked about. Here's how each requirement is implemented:

### 1. Heart Icon on Detail Screen

**Location**: `DetailScreen.kt` lines 74-95

```kotlin
IconButton(onClick = { viewModel.onFavoriteClick() }) {
    Icon(
        imageVector = if (uiState.isFavorite) {
            Icons.Filled.Favorite      // Filled heart when favorited
        } else {
            Icons.Filled.FavoriteBorder // Outline when not favorited
        },
        tint = if (uiState.isFavorite) Color.Red else MaterialTheme.colorScheme.onSurface
    )
}
```

### 2. Toggle Favorite on Click

**Location**: `DetailViewModel.kt` - `onFavoriteClick()`

```kotlin
fun onFavoriteClick() {
    _article.value?.let { currentArticle ->
        // Step 1: Determine new status
        val newFavoriteStatus = !_state.value.isFavorite

        // Step 2: Optimistic UI update (instant feedback)
        _state.value = _state.value.copy(isFavorite = newFavoriteStatus)

        // Step 3: Persist to database
        viewModelScope.launch {
            if (newFavoriteStatus) {
                saveArticleUseCase(article)   // Add to favorites
            } else {
                deleteArticleUseCase(article) // Remove from favorites
            }
        }
    }
}
```

### 3. Persistence Across Navigation

**How it works:**
1. When DetailScreen opens, `initArticle()` is called
2. `CheckFavoriteStatusUseCase` starts observing the database
3. The Flow emits the current favorite status from Room
4. ViewModel updates state, UI shows correct heart icon

```kotlin
fun initArticle(article: Article) {
    checkFavoriteStatusUseCase(article.url)
        .onEach { isFavorite ->
            _state.value = _state.value.copy(isFavorite = isFavorite)
        }
        .launchIn(viewModelScope)
}
```

### 4. Favorites List Synchronization

**Location**: `FavoritesViewModel.kt`

```kotlin
init {
    getFavoriteArticlesUseCase()
        .onEach { articles ->
            _state.value = FavoritesState(articles = articles)
        }
        .launchIn(viewModelScope)
}
```

This Flow is connected to Room database. When you add/remove a favorite in DetailScreen:
1. Room database changes
2. Room automatically re-runs the query
3. Flow emits new list
4. FavoritesViewModel updates state
5. FavoritesScreen recomposes with new list

### 5. Navigation from Favorites to Details

**Location**: `FavoritesScreen.kt`

```kotlin
ArticleItem(
    article = article,
    onItemClick = { clickedArticle ->
        // Article from favorites already has isFavorite = true
        navController.currentBackStackEntry?.savedStateHandle?.set("article", clickedArticle)
        navController.navigate(Screen.DetailScreen.route)
    }
)
```

When opening from favorites:
1. Article is passed with `isFavorite = true`
2. DetailScreen receives this article
3. `initArticle()` observes database (confirms it's favorited)
4. Heart icon shows filled

### 6. Instant UI Updates

Achieved through:
- **Optimistic updates**: UI changes immediately, before database operation completes
- **Flow observation**: Database changes trigger automatic UI updates
- **Compose recomposition**: State changes trigger UI redraws

---

## Navigation System

### How Navigation Works

```
┌─────────────────┐
│   NavGraph.kt   │  ← Defines all routes and screens
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│   NavHost       │  ← Container that displays current screen
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ NavController   │  ← Manages back stack and navigation
└─────────────────┘
```

### Passing Data Between Screens

We use `savedStateHandle` to pass the Article object:

```kotlin
// Sending screen (HomeScreen/FavoritesScreen):
navController.currentBackStackEntry?.savedStateHandle?.set("article", article)
navController.navigate(Screen.DetailScreen.route)

// Receiving screen (NavGraph → DetailScreen):
val article = navController.previousBackStackEntry?.savedStateHandle?.get<Article>("article")
DetailScreen(article = article)
```

### ViewModel Initialization

The key fix we made: DetailScreen calls `viewModel.initArticle(article)` using `LaunchedEffect`:

```kotlin
LaunchedEffect(key1 = article?.url) {
    article?.let {
        viewModel.initArticle(it)
    }
}
```

This ensures the ViewModel receives the article when the screen is first composed.

---

## State Management

### State Pattern

Each ViewModel has a state class:

```kotlin
// State class - immutable data object
data class DetailState(
    val isFavorite: Boolean = false
)

// In ViewModel
private val _state = mutableStateOf(DetailState())
val state: State<DetailState> = _state

// Updating state (creates new immutable object)
_state.value = _state.value.copy(isFavorite = true)
```

### Why Immutable State?

1. **Thread safety**: No race conditions
2. **Predictability**: State only changes in one place
3. **Debugging**: Easy to track state changes
4. **Compose optimization**: Can efficiently compare old/new states

### State Types Used

1. **`mutableStateOf()`**: For Compose integration
   - Triggers recomposition when value changes

2. **`MutableStateFlow`**: For observing from coroutines
   - Can use `collectAsState()` in Compose
   - Good for complex state objects

---

## Dependency Injection

### Hilt Modules

**NetworkModule.kt**: Provides Retrofit and OkHttp
```kotlin
@Provides
fun provideRetrofit(): Retrofit = Retrofit.Builder()
    .baseUrl("https://newsapi.org/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()
```

**DatabaseModule.kt**: Provides Room database and DAOs
```kotlin
@Provides
fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
    Room.databaseBuilder(context, AppDatabase::class.java, "news_db").build()
```

**AppModule.kt**: Provides Repository
```kotlin
@Provides
fun provideNewsRepository(impl: NewsRepositoryImpl): NewsRepository = impl
```

### How Hilt Knows What to Inject

1. `@Inject constructor`: Tells Hilt this class needs dependencies
2. `@Provides`: Tells Hilt how to create something
3. `@Binds`: Tells Hilt which implementation to use for an interface
4. `@Singleton`: Only create one instance

---

## How to Run the App

### Prerequisites
- Android Studio (latest version)
- Android device or emulator (API 24+)
- Internet connection (for API calls)

### Steps
1. Open project in Android Studio
2. Wait for Gradle sync to complete
3. Click Run (green play button)
4. Select device/emulator
5. App launches at Login screen

### Test Credentials
Use these email/password combinations:
- test@example.com / password123
- parag@icici.com / test123

---

## Common Issues and Solutions

### 1. "No article found in SavedStateHandle"
**Problem**: DetailViewModel tried to get article from wrong place
**Solution**: We added `initArticle()` method and call it from DetailScreen

### 2. Heart icon not updating
**Problem**: UI state not synced with database
**Solution**: Use Flow to observe database changes in real-time

### 3. Favorites list not refreshing
**Problem**: List wasn't observing database changes
**Solution**: Return Flow from Room DAO, observe in ViewModel

### 4. Duplicate favorites
**Problem**: Same article added multiple times
**Solution**: Use URL as primary key with REPLACE strategy

### 5. App crashes on rotation
**Problem**: Data lost on configuration change
**Solution**: Use ViewModel (survives rotation)

---

## Summary

This app demonstrates modern Android development with:

1. **Clean Architecture**: Clear separation of concerns
2. **MVVM Pattern**: UI observes ViewModel state
3. **Reactive Programming**: Flows for automatic updates
4. **Coroutines**: Async operations without callbacks
5. **Compose**: Declarative UI
6. **Hilt**: Dependency injection
7. **Room**: Local persistence

The favorites feature works by:
1. User taps heart → ViewModel updates state optimistically
2. ViewModel saves/deletes from Room database
3. Room Flows emit new data
4. All observing ViewModels receive updates
5. Compose recomposes UI with new state

This creates a seamless, reactive user experience where changes are instant and synchronized across all screens.
