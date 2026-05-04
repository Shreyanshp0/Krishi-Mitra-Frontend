package com.example.krishimitra.di

import android.content.Context
import com.example.krishimitra.data.api.CropApiService
import com.example.krishimitra.data.auth.AuthApi
import com.example.krishimitra.data.auth.AuthRepository
import com.example.krishimitra.data.local.InMemoryHistoryDataSource
import com.example.krishimitra.data.network.AndroidNetworkMonitor
import com.example.krishimitra.data.network.NetworkMonitor
import com.example.krishimitra.data.repository.CropRepositoryImpl
import com.example.krishimitra.domain.repository.CropRepository
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
    fun provideHistoryDataSource(): InMemoryHistoryDataSource {
        return InMemoryHistoryDataSource()
    }

    @Provides
    @Singleton
    fun provideNetworkMonitor(@ApplicationContext context: Context): NetworkMonitor {
        return AndroidNetworkMonitor(context)
    }

    @Provides
    @Singleton
    fun provideCropRepository(
        api: CropApiService,
        historyDataSource: InMemoryHistoryDataSource,
        networkMonitor: NetworkMonitor
    ): CropRepository {
        return CropRepositoryImpl(api, historyDataSource, networkMonitor)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(authApi: AuthApi): AuthRepository {
        return AuthRepository(authApi)
    }
}
