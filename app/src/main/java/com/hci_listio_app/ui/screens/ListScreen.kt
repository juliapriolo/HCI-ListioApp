package com.hci_listio_app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.hci_listio_app.R
import com.hci_listio_app.ui.Components.AddProductDialog
import com.hci_listio_app.ui.Components.EditItemDialog
import com.hci_listio_app.ui.Components.ListItem
import com.hci_listio_app.ui.Components.ListItemData
import com.hci_listio_app.ui.Components.ListioTopAppBar
import com.hci_listio_app.ui.Components.showToast
import com.hci_listio_app.ui.viewmodels.ListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    navController: NavController,
    listId: Long = 1L,
    listName: String = "Mi Lista",
    viewModel: ListViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = com.hci_listio_app.ui.Components.rememberAppSnackbarHostState()

    // Estados locales para diálogos
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showShareDialog by remember { mutableStateOf(false) }
    var itemToEdit by remember { mutableStateOf<ListItemData?>(null) }

    // Cargar la lista cuando se monta el composable
    LaunchedEffect(listId) {
        viewModel.loadList(listId)
    }

    // Mostrar mensajes de error
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { error ->
            snackbarHostState.showToast(error)
            viewModel.dismissError()
        }
    }

    Scaffold(
        containerColor = Color(0xFFFAFAFA),
        topBar = {
            ListioTopAppBar(
                title = uiState.listName.ifEmpty { listName },
                showBackButton = true,
                onBackClick = { navController.navigateUp() }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color(0xFF6DCB5A),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar producto")
            }
        },
        snackbarHost = { com.hci_listio_app.ui.Components.AppSnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header con avatares y estado
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Avatares de usuarios
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Avatar 1
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF6DCB5A)),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.perfilpredeterminado),
                                    contentDescription = "Usuario 1",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Avatar 2
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF6DCB5A)),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.perfilpredeterminado),
                                    contentDescription = "Usuario 2",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Botón añadir usuario
                            IconButton(
                                onClick = { showShareDialog = true },
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE0E0E0))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Añadir usuario",
                                    tint = Color.Gray
                                )
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            // Botón filtro
                            IconButton(onClick = { /* TODO: Filter options */ }) {
                                Icon(
                                    imageVector = Icons.Default.FilterList,
                                    contentDescription = "Opciones",
                                    tint = Color.Gray
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Estado de la lista y filtro
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.empty_list_icon),
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Lista ${uiState.completedCount}/${uiState.totalCount} Completada",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Todos",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Lista de items
                if (uiState.items.isEmpty() && !uiState.isLoading) {
                    // Estado vacío
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.empty_list_icon),
                            contentDescription = "Lista vacía",
                            tint = Color(0xFF6DCB5A),
                            modifier = Modifier.size(80.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Tu lista está vacía",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Empieza a agregar productos",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(uiState.items) { item ->
                            ListItem(
                                item = item,
                                onCheckedChange = { checked ->
                                    viewModel.toggleItemCheck(item.id, checked)
                                },
                                onMoreClick = {
                                    itemToEdit = item
                                    showEditDialog = true
                                }
                            )
                        }

                        // Botón agregar más productos al final
                        item {
                            Button(
                                onClick = { showAddDialog = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF6DCB5A)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Agregar más productos")
                            }
                        }
                    }
                }
            }

            // Indicador de carga
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF6DCB5A))
                }
            }
        }
    }

    // Diálogo para agregar producto
    if (showAddDialog) {
        AddProductDialog(
            onDismiss = { showAddDialog = false },
            onSave = { productName ->
                viewModel.addItem(productName)
                showAddDialog = false
            }
        )
    }

    // Diálogo para editar item
    if (showEditDialog && itemToEdit != null) {
        EditItemDialog(
            itemName = itemToEdit!!.name,
            quantity = "",
            unit = "24",
            brand = "Paty",
            store = "Supermercado Coto",
            onDismiss = {
                showEditDialog = false
                itemToEdit = null
            },
            onSave = { name, quantity, unit, brand, store ->
                viewModel.editItem(itemToEdit!!.id, name, quantity, unit, brand, store)
                showEditDialog = false
                itemToEdit = null
            }
        )
    }

    // Diálogo para compartir lista
    if (showShareDialog && itemToEdit != null) {
        EditItemDialog(
            itemName = itemToEdit!!.name,
            quantity = "",
            unit = "24",
            brand = "Paty",
            store = "Supermercado Coto",
            onDismiss = {
                showEditDialog = false
                itemToEdit = null
            },
            onSave = { name, quantity, unit, brand, store ->
                viewModel.editItem(itemToEdit!!.id, name, quantity, unit, brand, store)
                showEditDialog = false
                itemToEdit = null
            }
        )
    }
}