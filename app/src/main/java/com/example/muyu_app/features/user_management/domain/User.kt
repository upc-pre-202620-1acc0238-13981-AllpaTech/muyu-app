package com.example.muyu_app.features.user_management.domain

data class User(
    val email: String,
    val firstName: String,
    val lastName: String,
    val role: String,
    val token: String,
)
