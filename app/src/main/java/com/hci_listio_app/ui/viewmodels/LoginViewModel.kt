package com.hci_listio_app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hci_listio_app.data.AuthRepository
import com.hci_listio_app.data.AuthRepositoryProvider
import com.hci_listio_app.data.remote.UnverifiedAccountException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoggedIn: Boolean = false,
    val shouldNavigateToVerification: Boolean = false,
    val verificationEmail: String = ""
)

class LoginViewModel(
    private val authRepository: AuthRepository = AuthRepositoryProvider.instance
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value) }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun onSubmit() {
        val email = uiState.value.email.trim()
        val password = uiState.value.password

        if (email.isEmpty() || password.isEmpty()) {
            _uiState.update {
                it.copy(
                    errorMessage = "Ingresá un email y contraseña válidos."
                )
            }
            return
        }

        if (uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = authRepository.login(email, password)

            _uiState.update { current ->
                if (result.isSuccess) {
                    current.copy(
                        isLoading = false,
                        errorMessage = null,
                        isLoggedIn = true,
                        shouldNavigateToVerification = false,
                        verificationEmail = ""
                    )
                } else {
                    val exception = result.exceptionOrNull()
                    
                    if (exception is UnverifiedAccountException) {
                        current.copy(
                            isLoading = false,
                            errorMessage = null,
                            isLoggedIn = false,
                            shouldNavigateToVerification = true,
                            verificationEmail = exception.email
                        )
                    } else {
                        current.copy(
                            isLoading = false,
                            errorMessage = exception?.message ?: "Error desconocido.",
                            isLoggedIn = false,
                            shouldNavigateToVerification = false,
                            verificationEmail = ""
                        )
                    }
                }
            }
        }
    }

    fun consumeLoginSuccess() {
        _uiState.update { it.copy(isLoggedIn = false) }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun consumeNavigationToVerification() {
        _uiState.update { 
            it.copy(
                shouldNavigateToVerification = false,
                verificationEmail = ""
            ) 
        }
    }
}
