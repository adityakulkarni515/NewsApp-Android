package com.example.newsapp.data.api

import com.example.newsapp.data.model.NewsResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * This interface defines the contract for interacting with the News API using Retrofit.
 * It contains methods for fetching news data from the API endpoints.
 */
interface NewsApiService {

    /**
     * Fetches the top headlines from the News API.
     * This is a suspend function, making it suitable for use in coroutines.
     * @param apiKey The API key required for authenticating with the News API.
     * @param country The 2-letter ISO 3166-1 code of the country you want to get headlines for (e.g., "us").
     * @param page The page number of the results to fetch, for pagination.
     * @param pageSize The number of results to return per page.
     * @return A NewsResponse object containing the list of top headlines.
     */
    @GET("top-headlines")
    suspend fun getTopHeadlines(
        @Query("apiKey") apiKey: String,
        @Query("country") country: String = "us",
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): NewsResponse
}
