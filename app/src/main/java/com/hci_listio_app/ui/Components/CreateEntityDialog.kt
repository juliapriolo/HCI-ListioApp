package com.hci_listio_app.ui.Components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext
import com.hci_listio_app.R
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.launch

@Composable
fun CreateEntityDialog(
    title: String,
    nameLabel: String = "Nombre",
    showDescription: Boolean = false,
    descriptionLabel: String = "Descripción",
    showRecurring: Boolean = false,
    onDismiss: () -> Unit,
    onCreate: suspend (name: String, description: String?, recurring: Boolean) -> Long?
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var recurring by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White, shape = RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(text = title, modifier = Modifier.weight(1f))
                TextButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = stringResource(id = R.string.create_entity_close)) }
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it; error = null },
                label = { Text(nameLabel) },
                modifier = Modifier.fillMaxWidth()
            )

            if (showDescription) {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(descriptionLabel) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (showRecurring) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(text = stringResource(id = R.string.list_favorite_label), modifier = Modifier.weight(1f))
                    Switch(
                        checked = recurring,
                        onCheckedChange = { recurring = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF6DCB5A)
                        )
                    )
                }
            }

            error?.let { Text(text = it, color = Color.Red) }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onDismiss, colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF2E7D32))) { Text(stringResource(id = R.string.common_cancel)) }
                TextButton(onClick = {
                    if (name.isBlank()) {
                        error = context.getString(R.string.error_name_required)
                    } else {
                        isLoading = true
                        scope.launch {
                            try {
                                val createdId = onCreate(name.trim(), if (showDescription) description.trim() else null, recurring)
                                if (createdId != null) {
                                    onDismiss()
                                } else {
                                    error = context.getString(R.string.error_create_failed)
                                }
                            } catch (e: Exception) {
                                error = e.message ?: context.getString(R.string.error_unknown)
                            } finally {
                                isLoading = false
                            }
                        }
                    }
                }, enabled = !isLoading, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32), contentColor = Color.White)) {
                    if (isLoading) CircularProgressIndicator(modifier = Modifier.padding(4.dp), color = Color.White)
                    else Text(stringResource(id = R.string.common_save))
                }
            }
        }
    }
}
