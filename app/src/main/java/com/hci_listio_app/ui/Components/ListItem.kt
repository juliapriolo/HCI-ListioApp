package com.hci_listio_app.ui.Components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.hci_listio_app.R

data class ListItemData(
    val id: String,
    val name: String,
    val isChecked: Boolean = false,
    val productId: Long? = null,
    val quantity: Int? = null,
    val unit: String? = null,
)

@Composable
fun ListItem(
    item: ListItemData,
    onCheckedChange: (Boolean) -> Unit,
    onEditClick: (ListItemData) -> Unit,
    onDeleteClick: (ListItemData) -> Unit,
    isEditable: Boolean = true,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isChecked) Color(0xFFE8F5E9) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox
            Checkbox(
                checked = item.isChecked,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF6DCB5A),
                    uncheckedColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Item name
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyLarge,
                color = if (item.isChecked) Color.Gray else Color(0xFF333333),
                textDecoration = if (item.isChecked) TextDecoration.LineThrough else null,
                modifier = Modifier.weight(1f)
            )

            // More options button
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = stringResource(id = R.string.list_options),
                        tint = Color.Gray
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                        if (isEditable) {
                            DropdownMenuItem(
                                text = { Text(stringResource(id = R.string.edit_item_title)) },
                                onClick = {
                                    showMenu = false
                                    onEditClick(item)
                                }
                            )
                        }
                    DropdownMenuItem(
                        text = { Text(stringResource(id = R.string.confirm_delete), color = Color(0xFFD32F2F)) },
                        onClick = {
                            showMenu = false
                            onDeleteClick(item)
                        }
                    )

                }
            }
        }
    }
}
