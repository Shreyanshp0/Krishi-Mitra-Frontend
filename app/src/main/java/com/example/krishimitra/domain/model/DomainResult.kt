package com.example.krishimitra.domain.model

sealed interface DomainResult<out T> {
    data class Success<T>(val data: T) : DomainResult<T>
    data class Error(val message: String, val isOffline: Boolean = false) : DomainResult<Nothing>
}

