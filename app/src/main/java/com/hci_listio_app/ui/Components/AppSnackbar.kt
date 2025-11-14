package com.hci_listio_app.ui.Components

import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun rememberAppSnackbarHostState(): SnackbarHostState = remember { SnackbarHostState() }

@Composable
fun AppSnackbarHost(hostState: SnackbarHostState) {
    SnackbarHost(hostState = hostState)
}

suspend fun SnackbarHostState.showToast(message: String, duration: SnackbarDuration = SnackbarDuration.Short) {
    this.showSnackbar(message = message, duration = duration)
}
