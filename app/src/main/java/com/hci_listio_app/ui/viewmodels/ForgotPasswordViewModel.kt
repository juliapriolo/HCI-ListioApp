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

enum class ForgotPasswordStep {
    STEP_EMAIL,
    STEP_RESET
}

data class ForgotPasswordUiState(
    val email: String = "",
    val code: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val step: ForgotPasswordStep = ForgotPasswordStep.STEP_EMAIL,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isEmailSent: Boolean = false,
    val isPasswordReset: Boolean = false
)

class ForgotPasswordViewModel(
    private val authRepository: AuthRepository = AuthRepositoryProvider.instance
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, errorMessage = null) }
    }

    fun onCodeChange(value: String) {
        _uiState.update { it.copy(code = value, errorMessage = null) }
    }

    fun onNewPasswordChange(value: String) {
        _uiState.update { it.copy(newPassword = value, errorMessage = null) }
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.update { it.copy(confirmPassword = value, errorMessage = null) }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun onSubmit() {
        when (_uiState.value.step) {
            ForgotPasswordStep.STEP_EMAIL -> {
                handleEmailStep()
            }
            ForgotPasswordStep.STEP_RESET -> {
                handleResetStep()
            }
        }
    }

    private fun handleEmailStep() {
        val email = _uiState.value.email.trim()

        if (email.isEmpty()) {
            _uiState.update {
                it.copy(errorMessage = "Ingresá un email válido.")
            }
            return
        }

        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = authRepository.forgotPassword(email)

            _uiState.update { current ->
                if (result.isSuccess) {
                    current.copy(
                        isLoading = false,
                        errorMessage = null,
                        isEmailSent = true,
                        step = ForgotPasswordStep.STEP_RESET
                    )
                } else {
                    current.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "Error al enviar el código."
                    )
                }
            }
        }
    }

    private fun handleResetStep() {
        val code = _uiState.value.code.trim()
        val newPassword = _uiState.value.newPassword
        val confirmPassword = _uiState.value.confirmPassword

        if (code.isEmpty()) {
            _uiState.update {
                it.copy(errorMessage = "Ingresá el código de verificación.")
            }
            return
        }

        if (newPassword.isEmpty()) {
            _uiState.update {
                it.copy(errorMessage = "Ingresá una nueva contraseña.")
            }
            return
        }

        if (confirmPassword.isEmpty()) {
            _uiState.update {
                it.copy(errorMessage = "Confirmá tu nueva contraseña.")
            }
            return
        }

        if (newPassword != confirmPassword) {
            _uiState.update {
                it.copy(errorMessage = "Las contraseñas no coinciden.")
            }
            return
        }

        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = authRepository.resetPassword(code, newPassword)

            _uiState.update { current ->
                if (result.isSuccess) {
                    current.copy(
                        isLoading = false,
                        errorMessage = null,
                        isPasswordReset = true
                    )
                } else {
                    current.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "Error al restablecer la contraseña."
                    )
                }
            }
        }
    }

    fun consumeEmailSent() {
        _uiState.update { it.copy(isEmailSent = false) }
    }

    fun consumePasswordReset() {
        _uiState.update { it.copy(isPasswordReset = false) }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}

