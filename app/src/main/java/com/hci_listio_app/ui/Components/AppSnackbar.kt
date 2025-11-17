package com.hci_listio_app.ui.Components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp

@Composable
fun rememberAppSnackbarHostState(): SnackbarHostState = remember { SnackbarHostState() }

@Composable
fun AppSnackbarHost(hostState: SnackbarHostState) {
    
    SnackbarHost(hostState = hostState) { data ->
        
        
        Surface(
            color = Color(0xFF2E7D32),
            tonalElevation = 6.dp,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = data.visuals.message,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f)
                )

                data.visuals.actionLabel?.let { label ->
                    TextButton(onClick = { data.performAction() }) {
                        Text(text = label, color = Color.White)
                    }
                }
            }
        }
    }
}

suspend fun SnackbarHostState.showToast(message: String, duration: SnackbarDuration = SnackbarDuration.Short) {
    this.showSnackbar(message = message, duration = duration)
}
