package com.example.muyu_app.features.user_management.infrastructure.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthService {
    @POST("users/login")
    @Headers("Content-Type: application/json")
    suspend fun login(@Body loginRequestDto: LoginRequestDto): Response<LoginResponseDto>

    @POST("users/register")
    @Headers("Content-Type: application/json")
    suspend fun register(@Body registerRequestDto: RegisterRequestDto): Response<LoginResponseDto>
}
