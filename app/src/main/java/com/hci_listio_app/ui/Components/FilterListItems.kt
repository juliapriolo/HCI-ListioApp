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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

/**
 * Data class que representa los filtros para items de una lista.
 * Estos parámetros se envían directamente al backend.
 */
data class ListItemsFilter(
    val purchased: Boolean? = null,      // null = todos, true = comprados, false = pendientes
    val categoryId: Long? = null,        // Filtrar por categoría
    val search: String? = null,          // Búsqueda por nombre
    val sortBy: String? = null,          // Campo por el cual ordenar
    val order: String? = null            // "asc" o "desc"
) {
    fun isActive(): Boolean {
        return purchased != null ||
                categoryId != null ||
                !search.isNullOrEmpty() ||
                sortBy != null
    }

    fun getDescription(): String {
        val parts = mutableListOf<String>()

        when (purchased) {
            true -> parts.add("Comprados")
            false -> parts.add("Pendientes")
            null -> {}
        }

        if (categoryId != null) {
            parts.add("Por categoría")
        }

        if (!search.isNullOrEmpty()) {
            parts.add("\"$search\"")
        }

        if (sortBy != null) {
            val sortName = when(sortBy) {
                "name" -> "Nombre"
                "created_at" -> "Fecha"
                "quantity" -> "Cantidad"
                else -> sortBy
            }
            val orderName = if (order == "desc") "↓" else "↑"
            parts.add("$sortName $orderName")
        }

        return if (parts.isEmpty()) "Todos" else parts.joinToString(" • ")
    }
}

@Composable
fun FilterListItemsDialog(
    currentFilter: ListItemsFilter,
    categories: List<Pair<Long, String>>, // List of (id, name)
    onDismiss: () -> Unit,
    onApplyFilter: (ListItemsFilter) -> Unit
) {
    var selectedPurchased by remember { mutableStateOf(currentFilter.purchased) }
    var selectedCategoryId by remember { mutableStateOf(currentFilter.categoryId) }
    var searchQuery by remember { mutableStateOf(currentFilter.search ?: "") }
    var selectedSortBy by remember { mutableStateOf(currentFilter.sortBy) }
    var selectedOrder by remember { mutableStateOf(currentFilter.order ?: "asc") }

    var expandedCategoryMenu by remember { mutableStateOf(false) }
    var expandedSortMenu by remember { mutableStateOf(false) }

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
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Filtrar Items",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF303F4F)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = Color(0xFF6DCB5A)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Estado de compra
                Text(
                    text = "Estado de compra",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedPurchased == null,
                        onClick = { selectedPurchased = null },
                        label = { Text("Todos") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF6DCB5A),
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    FilterChip(
                        selected = selectedPurchased == false,
                        onClick = { selectedPurchased = false },
                        label = { Text("Pendientes") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF6DCB5A),
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    FilterChip(
                        selected = selectedPurchased == true,
                        onClick = { selectedPurchased = true },
                        label = { Text("Comprados") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF6DCB5A),
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Filtro por categoría
                if (categories.isNotEmpty()) {
                    Text(
                        text = "Categoría",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { expandedCategoryMenu = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF303F4F)
                            )
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (selectedCategoryId != null) {
                                        categories.find { it.first == selectedCategoryId }?.second
                                            ?: "Todas las categorías"
                                    } else {
                                        "Todas las categorías"
                                    },
                                    modifier = Modifier.weight(1f),
                                    color = if (selectedCategoryId != null)
                                        Color(0xFF303F4F)
                                    else
                                        Color.Gray
                                )
                                Icon(
                                    painter = painterResource(
                                        id = android.R.drawable.arrow_down_float
                                    ),
                                    contentDescription = "Expandir",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = expandedCategoryMenu,
                            onDismissRequest = { expandedCategoryMenu = false },
                            modifier = Modifier.fillMaxWidth(0.85f)
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "Todas las categorías",
                                        fontWeight = if (selectedCategoryId == null)
                                            FontWeight.Bold
                                        else
                                            FontWeight.Normal
                                    )
                                },
                                onClick = {
                                    selectedCategoryId = null
                                    expandedCategoryMenu = false
                                }
                            )

                            HorizontalDivider(color = Color(0xFFE0E0E0))

                            categories.forEach { (id, name) ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            name,
                                            fontWeight = if (selectedCategoryId == id)
                                                FontWeight.Bold
                                            else
                                                FontWeight.Normal
                                        )
                                    },
                                    onClick = {
                                        selectedCategoryId = id
                                        expandedCategoryMenu = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }

                // Ordenar por
                Text(
                    text = "Ordenar por",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Dropdown de ordenamiento
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedButton(
                            onClick = { expandedSortMenu = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF303F4F)
                            )
                        ) {
                            Text(
                                text = when(selectedSortBy) {
                                    "name" -> "Nombre"
                                    "created_at" -> "Fecha"
                                    "quantity" -> "Cantidad"
                                    else -> "Sin orden"
                                },
                                color = if (selectedSortBy != null)
                                    Color(0xFF303F4F)
                                else
                                    Color.Gray,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                painter = painterResource(
                                    id = android.R.drawable.arrow_down_float
                                ),
                                contentDescription = "Expandir",
                                tint = Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = expandedSortMenu,
                            onDismissRequest = { expandedSortMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Sin orden") },
                                onClick = {
                                    selectedSortBy = null
                                    expandedSortMenu = false
                                }
                            )
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("Nombre") },
                                onClick = {
                                    selectedSortBy = "name"
                                    expandedSortMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Fecha") },
                                onClick = {
                                    selectedSortBy = "created_at"
                                    expandedSortMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Cantidad") },
                                onClick = {
                                    selectedSortBy = "quantity"
                                    expandedSortMenu = false
                                }
                            )
                        }
                    }

                    // Botón de orden (asc/desc)
                    if (selectedSortBy != null) {
                        IconButton(
                            onClick = {
                                selectedOrder = if (selectedOrder == "asc") "desc" else "asc"
                            },
                            modifier = Modifier
                                .size(56.dp)
                        ) {
                            Text(
                                text = if (selectedOrder == "asc") "↑" else "↓",
                                fontSize = 24.sp,
                                color = Color(0xFF6DCB5A)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botones de acción
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            selectedPurchased = null
                            selectedCategoryId = null
                            searchQuery = ""
                            selectedSortBy = null
                            selectedOrder = "asc"
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF6DCB5A)
                        )
                    ) {
                        Text("Limpiar")
                    }

                    Button(
                        onClick = {
                            onApplyFilter(
                                ListItemsFilter(
                                    purchased = selectedPurchased,
                                    categoryId = selectedCategoryId,
                                    search = searchQuery.trim().ifEmpty { null },
                                    sortBy = selectedSortBy,
                                    order = if (selectedSortBy != null) selectedOrder else null
                                )
                            )
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6DCB5A)
                        )
                    ) {
                        Text(
                            "Aplicar",
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}