package com.example.muyu_app.features.user_management.presentation.login

import com.example.muyu_app.features.user_management.domain.User

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isHidden: Boolean = true,
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val user: User? = null,
    val errorMessage: String? = null,
)
