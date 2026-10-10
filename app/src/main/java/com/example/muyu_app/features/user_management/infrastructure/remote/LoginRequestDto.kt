package com.example.muyu_app.features.user_management.infrastructure.remote

data class LoginRequestDto(
    val email: String,
    val password: String,
)
