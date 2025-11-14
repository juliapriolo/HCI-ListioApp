package com.hci_listio_app.ui.Components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class OverviewItem(
    val id: String = "",
    val title: String,
    val isPrivate: Boolean = true,
    val completed: Int = 0,
    val total: Int = 0,
    val members: List<String> = emptyList(),
    val isFavorite: Boolean = false
)

@Composable
fun OverviewCard(
    item: OverviewItem,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    onToggleFavorite: (() -> Unit)? = null,
    onEdit: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    onRestore: (() -> Unit)? = null
) {
    val combinedModifier = if (onClick != null) modifier
        .fillMaxWidth()
        .clickable { onClick() }
    else modifier.fillMaxWidth()

    val optionsAvailable = onEdit != null || onDelete != null || onRestore != null
    val (menuExpanded, setMenuExpanded) = remember { mutableStateOf(false) }

    Card(
        modifier = combinedModifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = item.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = { onToggleFavorite?.invoke() }, enabled = onToggleFavorite != null) {
                        val icon = if (item.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder
                        Icon(imageVector = icon, contentDescription = "favorite", tint = if (item.isFavorite) Color(0xFF2E7D32) else Color.Gray)
                    }
                    if (optionsAvailable) {
                        Box {
                            IconButton(onClick = { setMenuExpanded(true) }) {
                                Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "Más opciones")
                            }
                            DropdownMenu(expanded = menuExpanded, onDismissRequest = { setMenuExpanded(false) }) {
                                if (onEdit != null) {
                                    DropdownMenuItem(text = { Text("Editar nombre") }, onClick = {
                                        setMenuExpanded(false)
                                        onEdit()
                                    })
                                }
                                if (onRestore != null) {
                                    DropdownMenuItem(text = { Text("Recuperar lista") }, onClick = {
                                        setMenuExpanded(false)
                                        onRestore()
                                    })
                                }
                                if (onDelete != null) {
                                    DropdownMenuItem(text = { Text("Eliminar lista", color = Color(0xFFD32F2F)) }, onClick = {
                                        setMenuExpanded(false)
                                        onDelete()
                                    })
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.size(6.dp))
                Text(text = if (item.isPrivate) "Privada" else "Pública", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.size(6.dp))
                Text(text = "Lista ${item.completed}/${item.total} Completada", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }

            Row(horizontalArrangement = Arrangement.spacedBy((-8).dp)) {
                item.members.take(3).forEach { name ->
                    val initials = name.split(' ').mapNotNull { it.firstOrNull()?.toString() }.joinToString("")
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(color = MaterialTheme.colorScheme.primaryContainer, shape = CircleShape)
                            ,
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = initials.uppercase(), style = MaterialTheme.typography.bodySmall)
                    }
                }
                if (item.members.size > 3) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(color = MaterialTheme.colorScheme.primaryContainer, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "+${item.members.size - 3}")
                    }
                }
            }
        }
    }
}
