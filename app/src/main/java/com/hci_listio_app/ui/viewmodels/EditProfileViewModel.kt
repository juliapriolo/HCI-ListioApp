package com.hci_listio_app.ui.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hci_listio_app.data.AuthRepository
import com.hci_listio_app.data.AuthRepositoryProvider
import com.hci_listio_app.data.UserRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EditProfileUiState(
    val photoBitmap: Bitmap? = null,
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isCurrentPasswordVisible: Boolean = false,
    val isNewPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val passwordsMatch: Boolean get() = newPassword == confirmPassword
}

sealed interface EditProfileEvent {
    object Saved : EditProfileEvent
}

class EditProfileViewModel(
    private val authRepository: AuthRepository = AuthRepositoryProvider.instance,
    private val userRepository: UserRepository = UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        EditProfileUiState(
            photoBitmap = userRepository.photoBitmap.value
        )
    )
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<EditProfileEvent>()
    val events: SharedFlow<EditProfileEvent> = _events.asSharedFlow()

    fun onCurrentPasswordChange(value: String) {
        _uiState.update { it.copy(currentPassword = value) }
    }

    fun onNewPasswordChange(value: String) {
        _uiState.update { it.copy(newPassword = value) }
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.update { it.copy(confirmPassword = value) }
    }

    fun toggleCurrentPasswordVisibility() {
        _uiState.update { it.copy(isCurrentPasswordVisible = !it.isCurrentPasswordVisible) }
    }

    fun toggleNewPasswordVisibility() {
        _uiState.update { it.copy(isNewPasswordVisible = !it.isNewPasswordVisible) }
    }

    fun toggleConfirmPasswordVisibility() {
        _uiState.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
    }

    fun onPhotoSelected(bitmap: Bitmap?) {
        _uiState.update { it.copy(photoBitmap = bitmap) }
    }

    fun saveChanges() {
        val state = _uiState.value
        
        // Validar que las contraseñas coincidan
        if (!state.passwordsMatch) {
            _uiState.update {
                it.copy(errorMessage = "Las contraseñas no coinciden.")
            }
            return
        }

        // Validar que si hay nueva contraseña, también haya contraseña actual
        if (state.newPassword.isNotEmpty() && state.currentPassword.isEmpty()) {
            _uiState.update {
                it.copy(errorMessage = "Debes ingresar tu contraseña actual para cambiarla.")
            }
            return
        }

        // Validar que la nueva contraseña no esté vacía si se intenta cambiar
        if (state.currentPassword.isNotEmpty() && state.newPassword.isEmpty()) {
            _uiState.update {
                it.copy(errorMessage = "Debes ingresar una nueva contraseña.")
            }
            return
        }

        if (state.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // Guardar la foto localmente (siempre se guarda)
            userRepository.updatePhoto(state.photoBitmap)

            // Cambiar contraseña en la API solo si hay nueva contraseña
            var passwordChangeSuccess = true
            if (state.newPassword.isNotEmpty()) {
                val result = authRepository.changePassword(state.currentPassword, state.newPassword)
                if (result.isSuccess) {
                    // Si el cambio de contraseña fue exitoso, también actualizar localmente
                    userRepository.updatePassword(state.newPassword)
                } else {
                    passwordChangeSuccess = false
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.exceptionOrNull()?.message ?: "Error al cambiar la contraseña."
                        )
                    }
                }
            }

            // Solo emitir evento de éxito si todo fue exitoso
            if (passwordChangeSuccess) {
                _uiState.update { it.copy(isLoading = false) }
                _events.emit(EditProfileEvent.Saved)
            }
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}


