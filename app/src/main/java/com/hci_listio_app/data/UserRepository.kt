package com.hci_listio_app.data

import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateOf

object UserRepository {
    val photoBitmap = mutableStateOf<Bitmap?>(null)
    val password = mutableStateOf("")

    fun updatePhoto(bitmap: Bitmap?) {
        photoBitmap.value = bitmap
    }

    fun updatePassword(newPassword: String) {
        password.value = newPassword
    }
}


