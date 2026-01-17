package com.example.newsapp.di

import com.example.newsapp.data.api.NewsApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * This Hilt module is responsible for providing network-related dependencies,
 * such as Retrofit, OkHttpClient, and the NewsApiService.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // The base URL for the News API.
    private const val BASE_URL = "https://newsapi.org/v2/"

    /**
     * Provides a singleton instance of HttpLoggingInterceptor.
     * This interceptor logs HTTP request and response data, which is useful for debugging.
     * @return An instance of HttpLoggingInterceptor.
     */
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        // Creates a new logging interceptor.
        return HttpLoggingInterceptor().apply {
            // Sets the logging level to BODY to log request and response headers and bodies.
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    /**
     * Provides a singleton instance of OkHttpClient.
     * This client is configured with the logging interceptor.
     * @param loggingInterceptor The interceptor for logging network traffic.
     * @return An instance of OkHttpClient.
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        // Builds a new OkHttpClient.
        return OkHttpClient.Builder()
            // Adds the logging interceptor to the client's interceptor chain.
            .addInterceptor(loggingInterceptor)
            // Builds and returns the OkHttpClient instance.
            .build()
    }

    /**
     * Provides a singleton instance of Retrofit.
     * Retrofit is configured with the base URL and a Gson converter factory.
     * @param okHttpClient The OkHttpClient to be used by Retrofit.
     * @return An instance of Retrofit.
     */
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        // Builds a new Retrofit instance.
        return Retrofit.Builder()
            // Sets the base URL for all API requests.
            .baseUrl(BASE_URL)
            // Sets the custom OkHttpClient.
            .client(okHttpClient)
            // Adds a converter factory to handle JSON serialization and deserialization using Gson.
            .addConverterFactory(GsonConverterFactory.create())
            // Builds and returns the Retrofit instance.
            .build()
    }

    /**
     * Provides a singleton instance of the NewsApiService.
     * This service is created by Retrofit and is used to make API calls.
     * @param retrofit The Retrofit instance to create the service.
     * @return An instance of NewsApiService.
     */
    @Provides
    @Singleton
    fun provideNewsApiService(retrofit: Retrofit): NewsApiService {
        // Creates and returns an implementation of the NewsApiService interface.
        return retrofit.create(NewsApiService::class.java)
    }
}
