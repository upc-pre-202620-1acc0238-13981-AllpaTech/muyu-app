package com.example.muyu_app.features.user_management.infrastructure.remote

data class LoginResponseDto(
    val token: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val role: String,
)
