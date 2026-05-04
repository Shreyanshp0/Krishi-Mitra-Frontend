package com.example.krishimitra.data.network.api

import com.example.krishimitra.data.dto.DistrictsResponseDto
import com.example.krishimitra.data.dto.StatesResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface LocationApi {
    @GET("api/locations/states")
    suspend fun getStates(): StatesResponseDto

    @GET("api/locations/districts")
    suspend fun getDistricts(@Query("state") state: String): DistrictsResponseDto
}
