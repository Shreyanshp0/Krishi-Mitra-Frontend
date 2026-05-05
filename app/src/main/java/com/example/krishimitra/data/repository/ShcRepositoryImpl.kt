package com.example.krishimitra.data.repository

import com.example.krishimitra.data.network.api.ShcApi
import com.example.krishimitra.domain.repository.ShcRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import android.util.Log
import java.io.File

class ShcRepositoryImpl(
    private val shcApi: ShcApi
) : ShcRepository {
    override suspend fun uploadShc(file: File): Result<Map<String, Any?>> {
        return try {
            // Validate file size (max 5MB)
            if (file.length() > 5 * 1024 * 1024) {
                return Result.failure(Exception("File size must be under 5MB"))
            }

            // Detect correct MIME type
            val mimeType = when {
                file.name.endsWith(".pdf", true) -> "application/pdf"
                file.name.endsWith(".jpg", true) || file.name.endsWith(".jpeg", true) -> "image/jpeg"
                file.name.endsWith(".png", true) -> "image/png"
                file.name.endsWith(".webp", true) -> "image/webp"
                file.name.endsWith(".bmp", true) -> "image/bmp"
                file.name.endsWith(".tiff", true) -> "image/tiff"
                else -> "application/octet-stream"
            }

            Log.d("SHC_UPLOAD", "Uploading file: ${file.name}, size: ${file.length()}, mimeType: $mimeType")

            val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
            
            val response = shcApi.uploadShc(body)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Log.e("SHC_UPLOAD", "Upload failed: ${response.message}")
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Log.e("SHC_UPLOAD", "Error during upload", e)
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
