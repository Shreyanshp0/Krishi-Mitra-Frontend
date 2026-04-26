package com.example.krishimitra.di

import android.content.Context
import com.example.krishimitra.BuildConfig
import com.example.krishimitra.data.api.CropApiService
import com.example.krishimitra.data.local.InMemoryHistoryDataSource
import com.example.krishimitra.data.network.AndroidNetworkMonitor
import com.example.krishimitra.data.repository.CropRepositoryImpl
import com.example.krishimitra.domain.usecase.GetCropRecommendationUseCase
import com.example.krishimitra.domain.usecase.GetHistoryUseCase
import com.example.krishimitra.domain.usecase.RetryRecommendationUseCase
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer(context: Context) {

    private val logger = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(logger)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService: CropApiService = retrofit.create(CropApiService::class.java)

    private val historyDataSource = InMemoryHistoryDataSource()
    private val networkMonitor = AndroidNetworkMonitor(context.applicationContext)
    private val repository = CropRepositoryImpl(apiService, historyDataSource, networkMonitor)

    val getCropRecommendationUseCase = GetCropRecommendationUseCase(repository)
    val getHistoryUseCase = GetHistoryUseCase(repository)
    val retryRecommendationUseCase = RetryRecommendationUseCase(repository)
}

