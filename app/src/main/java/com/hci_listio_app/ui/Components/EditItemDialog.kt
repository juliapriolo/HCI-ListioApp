package com.hci_listio_app.ui.Components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun EditItemDialog(
    itemName: String,
    quantity: String = "",
    unit: String = "",
    brand: String = "",
    store: String = "",
    onDismiss: () -> Unit,
    onSave: (name: String, quantity: String, unit: String, brand: String, store: String) -> Unit
) {
    var nameInput by remember { mutableStateOf(itemName) }
    var quantityInput by remember { mutableStateOf(quantity) }
    var unitInput by remember { mutableStateOf(unit) }
    var brandInput by remember { mutableStateOf(brand) }
    var storeInput by remember { mutableStateOf(store) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Header con título y botón cerrar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Editar Item",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF303F4F)
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = Color(0xFF6DCB5A)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Nombre del item
                Text(
                    text = nameInput,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF303F4F)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Cantidad y Unidades
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Cantidad
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Cantidad",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = quantityInput,
                            onValueChange = { quantityInput = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color(0xFFE0E0E0),
                                focusedBorderColor = Color(0xFF6DCB5A)
                            )
                        )
                    }

                    // Unidades
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Unidades",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = unitInput,
                            onValueChange = { unitInput = it },
                            placeholder = { Text("24", color = Color.Gray) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color(0xFFE0E0E0),
                                focusedBorderColor = Color(0xFF6DCB5A)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Marca
                Column {
                    Text(
                        text = "Marca:",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = brandInput,
                        onValueChange = { brandInput = it },
                        placeholder = { Text("Paty", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            focusedBorderColor = Color(0xFF6DCB5A)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Comprar en
                Column {
                    Text(
                        text = "Comprar en:",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = storeInput,
                        onValueChange = { storeInput = it },
                        placeholder = { Text("Supermercado Coto", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            focusedBorderColor = Color(0xFF6DCB5A)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botón Guardar
                Button(
                    onClick = {
                        onSave(nameInput, quantityInput, unitInput, brandInput, storeInput)
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6DCB5A)
                    )
                ) {
                    Text(
                        text = "Guardar",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
        }
    }
}
