package com.hci_listio_app.ui.Components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight

@Composable
fun ListHeader(
    username: String,
    modifier: Modifier = Modifier,
    headerColor: Color = Color(0xFF4FC46F)
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(96.dp)
            .background(color = headerColor),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = "¡Hola $username!",
            modifier = Modifier.padding(start = 20.dp),
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 24.sp, fontWeight = FontWeight.Bold),
            color = Color.White
        )
    }
}
