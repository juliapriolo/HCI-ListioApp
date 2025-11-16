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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.activity.compose.BackHandler
import androidx.navigation.NavController
import com.hci_listio_app.R
import com.hci_listio_app.ui.Components.AddItemToListDialog
import com.hci_listio_app.ui.Components.CreateProductDialog
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
    originTab: Int = -1,
    listName: String = "Mi Lista",
    viewModel: ListViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = com.hci_listio_app.ui.Components.rememberAppSnackbarHostState()
    val context = LocalContext.current
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
    var showAddItemDialog by remember { mutableStateOf(false) }
    var showCreateProductDialog by remember { mutableStateOf(false) }
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

    val navigateBack = remember(navController, originTab) {
        {
            if (originTab >= 0) {
                navController.previousBackStackEntry?.savedStateHandle?.set("overviewTab", originTab)
            }
            // popBackStack() returns Boolean; ensure our lambda returns Unit
            navController.popBackStack()
            kotlin.Unit
        }
    }

    BackHandler(onBack = navigateBack)

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
                onClick = { showAddItemDialog = true },
                containerColor = Color(0xFF6DCB5A),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(id = R.string.list_add_product))
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
                            if (displayedMembers.isEmpty()) {
                                MemberAvatar(
                                    description = stringResource(id = R.string.list_no_members),
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
                                    contentDescription = stringResource(id = R.string.list_add_user),
                                    tint = Color.Gray
                                )
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            // Botón filtro
                            IconButton(onClick = { /* TODO: Filter options */ }) {
                                Icon(
                                    imageVector = Icons.Default.FilterList,
                                    contentDescription = stringResource(id = R.string.list_options),
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
                            text = stringResource(id = R.string.list_completed, uiState.completedCount, uiState.totalCount),
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
                            text = stringResource(id = R.string.list_all),
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
                            contentDescription = stringResource(id = R.string.list_empty_title),
                            tint = Color(0xFF6DCB5A),
                            modifier = Modifier.size(80.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(id = R.string.list_empty_title),
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(id = R.string.list_empty_subtitle),
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
                                onEditClick = { selectedItem ->
                                    itemToEdit = selectedItem
                                    showEditDialog = true
                                },
                                onDeleteClick = { selectedItem ->
                                    viewModel.deleteItem(selectedItem.id)
                                }
                            )
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

    // Diálogo para agregar item (con productos existentes o crear nuevo)
    if (showAddItemDialog) {
        AddItemToListDialog(
            products = uiState.availableProducts,
            isLoadingProducts = uiState.isLoadingProducts,
            onDismiss = { showAddItemDialog = false },
            onSelectProduct = { product ->
                viewModel.addExistingProductToList(product)
                showAddItemDialog = false
            },
            onCreateNewProduct = {
                showAddItemDialog = false
                showCreateProductDialog = true
            }
        )
    }

    // Diálogo para crear nuevo producto
    if (showCreateProductDialog) {
        CreateProductDialog(
            categories = uiState.categories,
            isLoading = uiState.isCreatingProduct,
            onDismiss = { showCreateProductDialog = false },
            onCreateProduct = { productName, categoryId ->
                viewModel.createProductAndAddToList(productName, categoryId)
                showCreateProductDialog = false
            }
        )
    }

    // Diálogo para editar item
    if (showEditDialog && itemToEdit != null) {
        EditItemDialog(
            itemName = itemToEdit!!.name,
            quantity = itemToEdit!!.quantity?.toString() ?: "1",
            unit = "kg",
            onDismiss = {
                showEditDialog = false
                itemToEdit = null
            },
            onSave = { name, quantity, unit, brand, store ->
                viewModel.editItem(
                    itemId = itemToEdit!!.id,
                    quantity = quantity,
                    unit = unit
                )

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