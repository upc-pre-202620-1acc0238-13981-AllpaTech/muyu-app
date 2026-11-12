package com.example.muyu_app.features.user_management.infrastructure.remote

data class LoginResponseDto(
    val accessToken: String,
    val expiresAtUtc: String,
    val user: UserSummaryDto,
) {
    val token: String get() = accessToken
    val email: String get() = user.email
    val firstName: String get() = user.displayName.trim().substringBefore(" ")
    val lastName: String get() = user.displayName.trim().substringAfter(" ", "")
    val role: String get() = when (user.role.uppercase()) {
        "COMERCIANTE" -> "merchant"
        else -> "farmer"
    }
}

data class UserSummaryDto(
    val id: String,
    val email: String,
    val displayName: String,
    val role: String,
)
