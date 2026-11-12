package com.example.muyu_app.features.user_management.domain

/** The backend already has an account with this email. */
class EmailAlreadyRegisteredException : Exception()

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(
        name: String,
        email: String,
        phone: String,
        birthdate: String,
        password: String,
        dni: String,
    ): Result<User>
}
