package com.example.krishimitra.domain.repository

import java.io.File

interface ShcRepository {
    suspend fun uploadShc(file: File): Result<Map<String, Any?>>
    suspend fun getLatestShc(): Result<Map<String, Any?>>
}
