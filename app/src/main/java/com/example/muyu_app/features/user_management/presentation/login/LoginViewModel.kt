package com.example.muyu_app.features.user_management.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.muyu_app.features.user_management.application.LoginUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val signIn: LoginUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    fun onEmailChanged(email: String) {
        _state.update { it.copy(email = email, errorMessage = null) }
    }

    fun onPasswordChanged(password: String) {
        _state.update { it.copy(password = password, errorMessage = null) }
    }

    fun togglePasswordVisibility() {
        _state.update { it.copy(isHidden = !it.isHidden) }
    }

    fun login() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val result = signIn(state.value.email, state.value.password)
            result
                .onSuccess { user ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isAuthenticated = true,
                            user = user,
                        )
                    }
                }
                .onFailure {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isAuthenticated = false,
                            user = null,
                            errorMessage = "invalid",
                        )
                    }
                }
        }
    }

    fun consumeAuthentication() {
        _state.value = LoginUiState()
    }

    companion object {
        fun factory(signIn: LoginUseCase): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return LoginViewModel(signIn) as T
                }
            }
        }
    }
}
