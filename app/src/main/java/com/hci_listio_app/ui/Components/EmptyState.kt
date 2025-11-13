package com.hci_listio_app.ui.Components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hci_listio_app.R

@Composable
fun EmptyState(
    title: String = "Todavía no hay listas...",
    CTA: String = "¡Empieza a crear tu lista!",
    subtitle: String = "Tu lista aparecerá en esta sección",
    illustrationRes: Int = R.drawable.empty_list_icon,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(12.dp))
        Image(painter = painterResource(id = illustrationRes), contentDescription = null, modifier = Modifier.size(220.dp), contentScale = ContentScale.Fit)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = CTA, style = MaterialTheme.typography.bodyLarge)
        Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        Spacer(modifier = Modifier.height(32.dp))
    }
}
