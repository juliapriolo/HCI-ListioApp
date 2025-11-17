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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.hci_listio_app.R

data class ListItemsFilter(
    val purchased: Boolean? = null,      // null = todos, true = comprados, false = pendientes
    val categoryId: Long? = null,
    val search: String? = null,
    val sortBy: String? = null,
    val order: String? = null
)

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
                        text = stringResource(id = R.string.filter_items),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF303F4F)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = stringResource(id = R.string.close),
                            tint = Color(0xFF6DCB5A)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Estado de compra
                Text(
                    text = stringResource(id = R.string.purchase_status),
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
                        label = { Text(stringResource(id = R.string.all)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF6DCB5A),
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    FilterChip(
                        selected = selectedPurchased == false,
                        onClick = { selectedPurchased = false },
                        label = { Text(stringResource(id = R.string.pending)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF6DCB5A),
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    FilterChip(
                        selected = selectedPurchased == true,
                        onClick = { selectedPurchased = true },
                        label = { Text(stringResource(id = R.string.purchased)) },
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
                        text = stringResource(id = R.string.category),
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
                                            ?: stringResource(id = R.string.all_categories)
                                    } else {
                                        stringResource(id = R.string.all_categories)
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
                                    contentDescription = stringResource(id = R.string.expand),
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
                                        stringResource(id = R.string.all_categories),
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
                    text = stringResource(id = R.string.order_by),
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
                                    "name" -> stringResource(id = R.string.name)
                                    "created_at" -> stringResource(id = R.string.date)
                                    "quantity" -> stringResource(id = R.string.quantity)
                                    else -> stringResource(id = R.string.no_order)
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
                                contentDescription = stringResource(id = R.string.expand),
                                tint = Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = expandedSortMenu,
                            onDismissRequest = { expandedSortMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(id = R.string.no_order)) },
                                onClick = {
                                    selectedSortBy = null
                                    expandedSortMenu = false
                                }
                            )
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text(stringResource(id = R.string.name)) },
                                onClick = {
                                    selectedSortBy = "name"
                                    expandedSortMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(id = R.string.date)) },
                                onClick = {
                                    selectedSortBy = "created_at"
                                    expandedSortMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(id = R.string.quantity)) },
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
                        Text(stringResource(id = R.string.clean))
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
                            stringResource(id = R.string.apply),
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}