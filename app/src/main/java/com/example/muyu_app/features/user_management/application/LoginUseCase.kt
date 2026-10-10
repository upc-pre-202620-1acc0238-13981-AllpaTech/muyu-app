package com.example.muyu_app.features.user_management.application

import com.example.muyu_app.features.user_management.domain.AuthRepository

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String) = repository.login(email, password)
}
