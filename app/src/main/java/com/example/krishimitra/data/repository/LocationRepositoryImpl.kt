package com.example.krishimitra.data.repository

import com.example.krishimitra.data.network.api.LocationApi
import com.example.krishimitra.domain.repository.LocationRepository

class LocationRepositoryImpl(
    private val locationApi: LocationApi
) : LocationRepository {
    override suspend fun getStates(): Result<List<String>> {
        return try {
            val response = locationApi.getStates()
            if (response.success) {
                Result.success(response.states)
            } else {
                Result.failure(Exception("Failed to fetch states"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDistricts(state: String): Result<List<String>> {
        return try {
            val response = locationApi.getDistricts(state)
            if (response.success) {
                Result.success(response.districts)
            } else {
                Result.failure(Exception("Failed to fetch districts"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
