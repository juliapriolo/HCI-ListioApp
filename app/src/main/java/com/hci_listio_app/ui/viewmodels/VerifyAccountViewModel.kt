package com.hci_listio_app.ui.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hci_listio_app.R
import com.hci_listio_app.data.AuthRepository
import com.hci_listio_app.data.AuthRepositoryProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class VerifyAccountUiState(
    val code: String = "",
    val email: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isVerificationSuccessful: Boolean = false,
    val canResendCode: Boolean = true,
    val isResendingCode: Boolean = false
)

class VerifyAccountViewModel(
    private val application: Application,
    private val authRepository: AuthRepository = AuthRepositoryProvider.instance
) : ViewModel() {

    private val _uiState = MutableStateFlow(VerifyAccountUiState())
    val uiState: StateFlow<VerifyAccountUiState> = _uiState.asStateFlow()

    fun initialize(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun onCodeChange(value: String) {
        _uiState.update { it.copy(code = value, errorMessage = null) }
    }

    fun onResendCode() {
        val email = uiState.value.email
        if (email.isEmpty()) {
            _uiState.update {
                it.copy(errorMessage = application.getString(R.string.verify_account_error_email_unavailable))
            }
            return
        }

        if (uiState.value.isResendingCode) return

        viewModelScope.launch {
            _uiState.update { it.copy(isResendingCode = true, errorMessage = null) }

            val result = authRepository.sendVerification(email)

            _uiState.update { current ->
                if (result.isSuccess) {
                    current.copy(
                        isResendingCode = false,
                        errorMessage = null,
                        canResendCode = false
                    )
                } else {
                    current.copy(
                        isResendingCode = false,
                        errorMessage = result.exceptionOrNull()?.message ?: application.getString(R.string.verify_account_error_resend_failed)
                    )
                }
            }
        }
    }

    fun onSubmit() {
        val code = uiState.value.code.trim()

        if (code.isEmpty()) {
            _uiState.update {
                it.copy(errorMessage = application.getString(R.string.verify_account_error_code_required))
            }
            return
        }

        if (uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val verifyResult = authRepository.verifyAccount(code)

            _uiState.update { current ->
                if (verifyResult.isSuccess) {
                    current.copy(
                        isLoading = false,
                        errorMessage = null,
                        isVerificationSuccessful = true
                    )
                } else {
                    current.copy(
                        isLoading = false,
                        errorMessage = verifyResult.exceptionOrNull()?.message ?: application.getString(R.string.verify_account_error_verification_failed)
                    )
                }
            }
        }
    }

    fun consumeVerificationSuccess() {
        _uiState.update { it.copy(isVerificationSuccessful = false) }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}

