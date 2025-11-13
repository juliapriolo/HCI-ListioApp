package com.hci_listio_app.ui.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.hci_listio_app.data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ProfileUiState(
    val nombre: String = "Pedro",
    val email: String = "pedro@gmail.com",
    val photoBitmap: Bitmap? = null
)

class ProfileViewModel(
    private val userRepository: UserRepository = UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ProfileUiState(
            photoBitmap = userRepository.photoBitmap.value
        )
    )
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun updateUserInfo(nombre: String, email: String) {
        _uiState.update { it.copy(nombre = nombre, email = email) }
    }

    fun updatePhoto(bitmap: Bitmap?) {
        userRepository.updatePhoto(bitmap)
        _uiState.update { it.copy(photoBitmap = bitmap) }
    }
}


