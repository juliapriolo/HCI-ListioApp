package com.hci_listio_app.ui.Components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.icons.Icons
import androidx.compose.material3.icons.filled.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class OverviewItem(
    val title: String,
    val isPrivate: Boolean = true,
    val completed: Int = 0,
    val total: Int = 0,
    val members: List<String> = emptyList()
)

@Composable
fun OverviewCard(item: OverviewItem, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            androidx.compose.foundation.layout.Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = item.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(imageVector = Icons.Default.FavoriteBorder, contentDescription = "favorite")
                }
                Spacer(modifier = Modifier.size(6.dp))
                Text(text = if (item.isPrivate) "Privada" else "Pública", color = Color(0xFF4FC46F), style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.size(6.dp))
                Text(text = "Lista ${item.completed}/${item.total} Completada", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }

            Row(horizontalArrangement = Arrangement.spacedBy((-8).dp)) {
                item.members.take(3).forEach { name ->
                    val initials = name.split(' ').mapNotNull { it.firstOrNull()?.toString() }.joinToString("")
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(color = Color(0xFFEDE7F6), shape = CircleShape)
                            .clickable { },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = initials.uppercase(), style = MaterialTheme.typography.bodySmall)
                    }
                }
                if (item.members.size > 3) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(color = Color(0xFFF3E8FF), shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "+${item.members.size - 3}")
                    }
                }
            }
        }
    }
}
