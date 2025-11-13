package com.hci_listio_app.ui.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hci_listio_app.data.AuthRepository
import com.hci_listio_app.data.AuthRepositoryProvider
import com.hci_listio_app.data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val nombre: String = "",
    val apellido: String = "",
    val email: String = "",
    val photoBitmap: Bitmap? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoggedOut: Boolean = false
)

class ProfileViewModel(
    private val authRepository: AuthRepository = AuthRepositoryProvider.instance,
    private val userRepository: UserRepository = UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ProfileUiState(
            photoBitmap = userRepository.photoBitmap.value
        )
    )
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            val token = authRepository.authToken.value
            if (token == null) {
                _uiState.update {
                    it.copy(
                        errorMessage = "No hay sesión activa. Por favor, inicia sesión.",
                        isLoading = false
                    )
                }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = authRepository.getProfile()

            _uiState.update { current ->
                if (result.isSuccess) {
                    val profile = result.getOrNull()!!
                    current.copy(
                        nombre = profile.name,
                        apellido = profile.surname,
                        email = profile.email,
                        isLoading = false,
                        errorMessage = null
                    )
                } else {
                    current.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "Error al cargar el perfil."
                    )
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = authRepository.logout()

            _uiState.update { current ->
                if (result.isSuccess) {
                    current.copy(
                        isLoading = false,
                        isLoggedOut = true
                    )
                } else {
                    current.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "Error al cerrar sesión."
                    )
                }
            }
        }
    }

    fun consumeLogoutSuccess() {
        _uiState.update { it.copy(isLoggedOut = false) }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun updatePhoto(bitmap: Bitmap?) {
        userRepository.updatePhoto(bitmap)
        _uiState.update { it.copy(photoBitmap = bitmap) }
    }
}


