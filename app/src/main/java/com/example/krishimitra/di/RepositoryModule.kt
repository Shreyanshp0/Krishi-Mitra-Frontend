package com.example.krishimitra.di

import android.content.Context
import com.example.krishimitra.data.network.api.AuthApi
import com.example.krishimitra.data.network.api.LocationApi
import com.example.krishimitra.data.network.api.RecommendApi
import com.example.krishimitra.data.network.api.ShcApi
import com.example.krishimitra.data.network.api.WeatherApi
import com.example.krishimitra.data.auth.AuthRepository
import com.example.krishimitra.data.local.HistoryManager
import com.example.krishimitra.data.network.AndroidNetworkMonitor
import com.example.krishimitra.data.network.NetworkMonitor
import com.example.krishimitra.data.repository.ChatRepositoryImpl
import com.example.krishimitra.data.repository.CropRepositoryImpl
import com.example.krishimitra.data.repository.LocationRepositoryImpl
import com.example.krishimitra.data.repository.ShcRepositoryImpl
import com.example.krishimitra.data.repository.WeatherRepositoryImpl
import com.example.krishimitra.domain.repository.ChatRepository
import com.example.krishimitra.domain.repository.CropRepository
import com.example.krishimitra.domain.repository.LocationRepository
import com.example.krishimitra.domain.repository.ShcRepository
import com.example.krishimitra.domain.repository.WeatherRepository
import com.example.krishimitra.data.network.api.ChatApi
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }

    @Provides
    @Singleton
    fun provideNetworkMonitor(@ApplicationContext context: Context): NetworkMonitor {
        return AndroidNetworkMonitor(context)
    }

    @Provides
    @Singleton
    fun provideCropRepository(
        api: RecommendApi,
        historyManager: HistoryManager,
        networkMonitor: NetworkMonitor
    ): CropRepository {
        return CropRepositoryImpl(api, historyManager, networkMonitor)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(authApi: AuthApi): AuthRepository {
        return AuthRepository(authApi)
    }

    @Provides
    @Singleton
    fun provideLocationRepository(locationApi: LocationApi): LocationRepository {
        return LocationRepositoryImpl(locationApi)
    }

    @Provides
    @Singleton
    fun provideShcRepository(shcApi: ShcApi): ShcRepository {
        return ShcRepositoryImpl(shcApi)
    }

    @Provides
    @Singleton
    fun provideWeatherRepository(weatherApi: WeatherApi): WeatherRepository {
        return WeatherRepositoryImpl(weatherApi)
    }

    @Provides
    @Singleton
    fun provideChatRepository(chatApi: ChatApi): ChatRepository {
        return ChatRepositoryImpl(chatApi)
    }
}
