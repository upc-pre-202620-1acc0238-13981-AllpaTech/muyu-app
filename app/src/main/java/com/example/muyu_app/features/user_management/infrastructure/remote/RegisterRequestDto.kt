package com.example.muyu_app.features.user_management.infrastructure.remote

data class RegisterRequestDto(
    val name: String,
    val email: String,
    val phone: String,
    val birthdate: String,
    val password: String,
    val dni: String,
)

data class RegisterResponseDto(
    val id: String,
    val email: String,
    val displayName: String,
    val role: String,
)
