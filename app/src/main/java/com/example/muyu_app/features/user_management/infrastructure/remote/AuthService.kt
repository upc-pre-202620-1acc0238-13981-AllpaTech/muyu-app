package com.example.muyu_app.features.user_management.infrastructure.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthService {
    @POST("auth/login")
    @Headers("Content-Type: application/json")
    suspend fun login(@Body loginRequestDto: LoginRequestDto): Response<LoginResponseDto>

    @POST("auth/register")
    @Headers("Content-Type: application/json")
    suspend fun register(@Body registerRequestDto: RegisterRequestDto): Response<RegisterResponseDto>

    @POST("auth/change-password")
    @Headers("Content-Type: application/json")
    suspend fun changePassword(@Body request: ChangePasswordRequestDto): Response<Unit>
}

data class ChangePasswordRequestDto(
    val currentPassword: String,
    val newPassword: String,
)
