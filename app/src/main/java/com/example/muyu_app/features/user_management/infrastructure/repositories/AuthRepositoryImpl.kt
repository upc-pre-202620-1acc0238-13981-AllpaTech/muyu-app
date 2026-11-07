package com.example.muyu_app.features.user_management.infrastructure.repositories

import com.example.muyu_app.core.network.BackendStateStore
import com.example.muyu_app.core.network.UserRecord
import com.example.muyu_app.core.network.MobileStateService
import com.example.muyu_app.features.user_management.domain.AuthRepository
import com.example.muyu_app.features.user_management.domain.User
import com.example.muyu_app.features.user_management.infrastructure.remote.AuthService
import com.example.muyu_app.features.user_management.infrastructure.remote.LoginRequestDto
import com.example.muyu_app.features.user_management.infrastructure.remote.LoginResponseDto
import com.example.muyu_app.features.user_management.infrastructure.remote.RegisterRequestDto

class AuthRepositoryImpl(
    private val service: AuthService,
    private val store: BackendStateStore,
    private val stateService: MobileStateService,
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val response = service.login(LoginRequestDto(email, password))
            val dto = response.body()
            if (response.isSuccessful && dto != null) {
                updateSession(dto, password)
                loadServerState(dto)
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
        dni: String,
    ): Result<User> {
        return try {
            val response = service.register(
                RegisterRequestDto(name, email, phone, birthdate, password, dni),
            )
            if (response.isSuccessful) {
                login(email, password)
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
        val existing = store.users.find { it.email.equals(dto.email, ignoreCase = true) }
        val record = UserRecord(
            email = dto.email,
            password = "",
            firstName = dto.firstName,
            lastName = dto.lastName,
            role = dto.role,
            phone = phone.ifBlank { existing?.phone.orEmpty() },
            birthdate = birthdate.ifBlank { existing?.birthdate.orEmpty() },
            avatarUri = existing?.avatarUri.orEmpty(),
            companyName = existing?.companyName.orEmpty(),
            companyActivity = existing?.companyActivity.orEmpty(),
            ruc = existing?.ruc.orEmpty(),
            location = existing?.location.orEmpty(),
            companyEmail = existing?.companyEmail.orEmpty(),
        )
        val existingIndex = store.users.indexOfFirst {
            it.email.equals(dto.email, ignoreCase = true)
        }
        if (existingIndex >= 0) {
            store.users[existingIndex] = record
        } else {
            store.users.add(record)
        }
        store.persist()
        store.startSession(record, dto.token)
    }

    private suspend fun loadServerState(dto: LoginResponseDto) {
        val response = stateService.getState()
        val remote = response.body()
        if (response.isSuccessful && remote != null) {
            store.replaceWith(remote)
            loadSharedData()
            val authenticated = store.users.find { it.email.equals(dto.email, ignoreCase = true) }
                ?: UserRecord(
                    email = dto.email,
                    password = "",
                    firstName = dto.firstName,
                    lastName = dto.lastName,
                    role = dto.role,
                ).also(store.users::add)
            store.startSession(authenticated, dto.token)
        } else if (response.code() == 204) {
            stateService.putState(store.snapshot())
            loadSharedData()
        } else if (!response.isSuccessful) {
            throw IllegalStateException("Unable to load server state: ${response.code()}")
        }
    }

    private suspend fun loadSharedData() {
        val plotsResponse = stateService.getPlots()
        if (!plotsResponse.isSuccessful) {
            throw IllegalStateException("Unable to load remote plots: ${plotsResponse.code()}")
        }
        store.plots.clear()
        store.plots.addAll(plotsResponse.body().orEmpty())

        val visitsResponse = stateService.getVisits()
        if (!visitsResponse.isSuccessful) {
            throw IllegalStateException("Unable to load remote visits: ${visitsResponse.code()}")
        }
        store.visits.clear()
        store.visits.addAll(visitsResponse.body().orEmpty())
    }
}
