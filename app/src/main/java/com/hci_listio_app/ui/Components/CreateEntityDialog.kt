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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.launch

/**
 * Generic dialog for creating entities (lists, products, etc.).
 * - title: dialog title
 * - nameLabel: label for main text field
 * - showDescription: whether to show an additional description field
 * - onDismiss: called when dialog is dismissed
 * - onCreate: suspend function that should create the entity and return an id (Long) or null on failure
 */
@Composable
fun CreateEntityDialog(
    title: String,
    nameLabel: String = "Nombre",
    showDescription: Boolean = false,
    descriptionLabel: String = "Descripción",
    onDismiss: () -> Unit,
    onCreate: suspend (name: String, description: String?) -> Long?
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

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
                TextButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = "Cerrar") }
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

            error?.let { Text(text = it, color = Color.Red) }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onDismiss) { Text("Cancelar") }
                Button(onClick = {
                    if (name.isBlank()) {
                        error = "El nombre no puede estar vacío"
                        return@Button
                    }
                    isLoading = true
                    // launch coroutine to create
                    scope.launch {
                        try {
                            val createdId = onCreate(name.trim(), if (showDescription) description.trim() else null)
                            if (createdId != null) {
                                // success -> dismiss handled by caller (they will navigate)
                                onDismiss()
                            } else {
                                error = "No se pudo crear. Intenta nuevamente."
                            }
                        } catch (e: Exception) {
                            error = e.message ?: "Error desconocido"
                        } finally {
                            isLoading = false
                        }
                    }
                }, enabled = !isLoading) {
                    if (isLoading) CircularProgressIndicator(modifier = Modifier.padding(4.dp))
                    else Text("Guardar")
                }
            }
        }
    }
}
