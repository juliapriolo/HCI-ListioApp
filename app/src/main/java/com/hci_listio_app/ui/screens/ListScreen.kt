package com.hci_listio_app.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import com.hci_listio_app.R
import com.hci_listio_app.data.ListHistoryManager
import com.hci_listio_app.ui.Components.AddProductDialog
import com.hci_listio_app.ui.Components.EditItemDialog
import com.hci_listio_app.ui.Components.ListItem
import com.hci_listio_app.ui.Components.ListItemData
import com.hci_listio_app.ui.Components.ListioTopAppBar
import com.hci_listio_app.ui.Components.showToast
import com.hci_listio_app.ui.viewmodels.ListViewModel
import com.hci_listio_app.ui.Components.ShareListDialog
import com.hci_listio_app.ui.Components.SharedUser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    navController: NavController,
    listId: Long = 1L,
    listName: String = "Mi Lista",
    originTab: Int = -1,
    viewModel: ListViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = com.hci_listio_app.ui.Components.rememberAppSnackbarHostState()
    val archivedIds by ListHistoryManager.archivedListIds.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val isArchived = uiState.listId?.let { archivedIds.contains(it) } ?: false
    val showCompletedBanner = !isArchived && uiState.totalCount > 0 && uiState.completedCount == uiState.totalCount
    val shareDialogUsers = remember(uiState.owner, uiState.sharedMembers) {
        val seenIds = mutableSetOf<Long>()
        val participants = mutableListOf<SharedUser>()

        uiState.owner?.let { owner ->
            if (seenIds.add(owner.id)) {
                participants.add(
                    SharedUser(
                        id = owner.id,
                        name = buildUserDisplayName(owner.name, owner.surname, owner.email),
                        role = "Creador"
                    )
                )
            }
        }

        uiState.sharedMembers.forEach { member ->
            if (seenIds.add(member.id)) {
                participants.add(
                    SharedUser(
                        id = member.id,
                        name = buildUserDisplayName(member.name, member.surname, member.email),
                        role = "Editor"
                    )
                )
            }
        }

        participants.toList()
    }
    val displayedMembers = remember(shareDialogUsers) { shareDialogUsers.take(3) }

    // Estados locales para diálogos
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showShareDialog by remember { mutableStateOf(false) }
    var itemToEdit by remember { mutableStateOf<ListItemData?>(null) }

    val navigateBack = remember(navController, originTab) {
        {
            if (originTab >= 0) {
                navController.previousBackStackEntry?.savedStateHandle?.set("overviewTab", originTab)
            }
            navController.popBackStack()
            Unit
        }
    }

    BackHandler(onBack = navigateBack)

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
                onBackClick = navigateBack
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
                if (showCompletedBanner) {
                    CompletedListBanner(
                        onMoveToHistory = {
                            uiState.listId?.let { listId ->
                                ListHistoryManager.moveToHistory(listId)
                                coroutineScope.launch {
                                    snackbarHostState.showToast("Lista movida al historial")
                                }
                                navigateBack()
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
 
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
                            if (displayedMembers.isEmpty()) {
                                MemberAvatar(
                                    description = "Sin integrantes",
                                    modifier = Modifier.size(40.dp)
                                )
                            } else {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    displayedMembers.forEach { member ->
                                        MemberAvatar(
                                            description = member.name,
                                            modifier = Modifier.size(40.dp)
                                        )
                                    }
                                }
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

    if (showAddDialog) {
        AddProductDialog(
            onDismiss = { showAddDialog = false },
            onSave = { name, brand ->
                // Si querés guardar la marca en metadata, queda así:
                viewModel.addItem(
                    name = name,
                    brand = brand
                )
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
    if (showShareDialog) {
        ShareListDialog(
            currentUsers = shareDialogUsers,
            onDismiss = { showShareDialog = false },
            onShare = { email ->
                viewModel.shareListWithUser(email)
            },
            onRemoveUser = { userId ->
                viewModel.removeUserFromList(userId)
            }
        )
    }
}

private fun buildUserDisplayName(name: String, surname: String, fallback: String): String {
    val parts = listOf(name.trim(), surname.trim()).filter { it.isNotEmpty() }
    val fullName = parts.joinToString(" ")
    return fullName.ifBlank { fallback.ifBlank { "Usuario" } }
}

@Composable
private fun MemberAvatar(
    description: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(Color(0xFF6DCB5A)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.perfilpredeterminado),
            contentDescription = description,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
private fun CompletedListBanner(onMoveToHistory: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Lista completada",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF388E3C)
                )
                Text(
                    text = "¿Mover al historial?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF388E3C)
                )
            }
            IconButton(onClick = onMoveToHistory) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Mover al historial",
                    tint = Color(0xFF388E3C)
                )
            }
        }
    }
}
