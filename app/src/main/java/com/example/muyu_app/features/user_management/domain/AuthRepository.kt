package com.example.muyu_app.features.user_management.domain

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(
        name: String,
        email: String,
        phone: String,
        birthdate: String,
        password: String,
    ): Result<User>
}
