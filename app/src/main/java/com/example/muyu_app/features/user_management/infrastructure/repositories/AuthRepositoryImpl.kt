package com.example.muyu_app.features.user_management.infrastructure.repositories

import com.example.muyu_app.core.network.FakeApiStore
import com.example.muyu_app.core.network.UserRecord
import com.example.muyu_app.features.user_management.domain.AuthRepository
import com.example.muyu_app.features.user_management.domain.User
import com.example.muyu_app.features.user_management.infrastructure.remote.AuthService
import com.example.muyu_app.features.user_management.infrastructure.remote.LoginRequestDto
import com.example.muyu_app.features.user_management.infrastructure.remote.LoginResponseDto
import com.example.muyu_app.features.user_management.infrastructure.remote.RegisterRequestDto

class AuthRepositoryImpl(
    private val service: AuthService,
    private val store: FakeApiStore,
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val response = service.login(LoginRequestDto(email, password))
            val dto = response.body()
            if (response.isSuccessful && dto != null) {
                updateSession(dto, password)
                Result.success(dto.toUser())
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(
        name: String,
        email: String,
        phone: String,
        birthdate: String,
        password: String,
    ): Result<User> {
        return try {
            val response = service.register(
                RegisterRequestDto(name, email, phone, birthdate, password),
            )
            val dto = response.body()
            if (response.isSuccessful && dto != null) {
                updateSession(
                    dto = dto,
                    password = password,
                    phone = phone,
                    birthdate = birthdate,
                )
                Result.success(dto.toUser())
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun LoginResponseDto.toUser() = User(
        email = email,
        firstName = firstName,
        lastName = lastName,
        role = role,
        token = token,
    )

    private fun updateSession(
        dto: LoginResponseDto,
        password: String,
        phone: String = "",
        birthdate: String = "",
    ) {
        val record = UserRecord(
            email = dto.email,
            password = password,
            firstName = dto.firstName,
            lastName = dto.lastName,
            role = dto.role,
            phone = phone,
            birthdate = birthdate,
        )
        val existingIndex = store.users.indexOfFirst {
            it.email.equals(dto.email, ignoreCase = true)
        }
        if (existingIndex >= 0) {
            store.users[existingIndex] = record
        } else {
            store.users.add(record)
        }
        store.currentUser = record
        store.token = dto.token
        store.persist()
    }
}
