package com.example.muyu_app.features.user_management.application

import com.example.muyu_app.features.user_management.domain.AuthRepository

class RegisterUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(
        name: String,
        email: String,
        phone: String,
        birthdate: String,
        password: String,
    ) = repository.register(name, email, phone, birthdate, password)
}
