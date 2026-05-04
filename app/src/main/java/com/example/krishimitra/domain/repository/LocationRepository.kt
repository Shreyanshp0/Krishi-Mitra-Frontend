package com.example.krishimitra.domain.repository

interface LocationRepository {
    suspend fun getStates(): Result<List<String>>
    suspend fun getDistricts(state: String): Result<List<String>>
}
