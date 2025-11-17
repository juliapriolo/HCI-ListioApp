package com.hci_listio_app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hci_listio_app.data.AuthRepository
import com.hci_listio_app.data.AuthRepositoryProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SignUpUiState(
    val nombre: String = "",
    val apellido: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isRegistrationSuccessful: Boolean = false,
    val registeredEmail: String = "",
    val registeredPassword: String = ""
)

class SignUpViewModel(
    private val authRepository: AuthRepository = AuthRepositoryProvider.instance
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    fun onNombreChange(value: String) {
        _uiState.update { it.copy(nombre = value, errorMessage = null) }
    }

    fun onApellidoChange(value: String) {
        _uiState.update { it.copy(apellido = value, errorMessage = null) }
    }

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, errorMessage = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, errorMessage = null) }
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.update { it.copy(confirmPassword = value, errorMessage = null) }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun toggleConfirmPasswordVisibility() {
        _uiState.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
    }

    fun onSubmit() {
        val nombre = uiState.value.nombre.trim()
        val apellido = uiState.value.apellido.trim()
        val email = uiState.value.email.trim()
        val password = uiState.value.password
        val confirmPassword = uiState.value.confirmPassword

        
        if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            _uiState.update {
                it.copy(
                    errorMessage = "Completá todos los campos."
                )
            }
            return
        }

        
        if (!email.contains("@")) {
            _uiState.update {
                it.copy(
                    errorMessage = "Ingresá un email válido."
                )
            }
            return
        }

        
        if (password != confirmPassword) {
            _uiState.update {
                it.copy(
                    errorMessage = "Las contraseñas no coinciden."
                )
            }
            return
        }

        if (uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = authRepository.register(nombre, apellido, email, password)

            _uiState.update { current ->
                if (result.isSuccess) {
                    current.copy(
                        isLoading = false,
                        errorMessage = null,
                        isRegistrationSuccessful = true,
                        registeredEmail = email,
                        registeredPassword = password
                    )
                } else {
                    current.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "Error desconocido.",
                        isRegistrationSuccessful = false
                    )
                }
            }
        }
    }

    fun consumeRegistrationSuccess() {
        _uiState.update { it.copy(isRegistrationSuccessful = false) }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}


