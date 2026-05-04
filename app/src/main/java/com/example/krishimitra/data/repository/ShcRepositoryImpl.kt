package com.example.krishimitra.data.repository

import com.example.krishimitra.data.network.api.ShcApi
import com.example.krishimitra.domain.repository.ShcRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class ShcRepositoryImpl(
    private val shcApi: ShcApi
) : ShcRepository {
    override suspend fun uploadShc(file: File): Result<Map<String, Any?>> {
        return try {
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
            val response = shcApi.uploadShc(body)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getLatestShc(): Result<Map<String, Any?>> {
        return try {
            val response = shcApi.getLatestShc()
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
