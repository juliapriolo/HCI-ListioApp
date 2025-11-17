package com.hci_listio_app.ui.screens

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
import com.hci_listio_app.ui.Components.FilterListItemsDialog
import com.hci_listio_app.ui.Components.ListItem
import com.hci_listio_app.ui.Components.ListItemData
import com.hci_listio_app.ui.Components.ListioTopAppBar
import com.hci_listio_app.ui.Components.showToast
import com.hci_listio_app.ui.viewmodels.ListViewModel
import com.hci_listio_app.ui.Components.ShareListDialog
import com.hci_listio_app.data.ListHistoryManager
import androidx.compose.runtime.collectAsState
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

    var showAddItemDialog by remember { mutableStateOf(false) }
    var showCreateProductDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showShareDialog by remember { mutableStateOf(false) }
    var showFilterDialog by remember { mutableStateOf(false) }
    var itemToEdit by remember { mutableStateOf<ListItemData?>(null) }

    LaunchedEffect(listId) {
        viewModel.loadList(listId)
    }

    val archivedIds by ListHistoryManager.archivedListIds.collectAsState(initial = emptySet<Long>())
    val isArchived = archivedIds.contains(listId)

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
            if (!isArchived) {
                FloatingActionButton(
                    onClick = { showAddItemDialog = true },
                    containerColor = Color.White,
                    contentColor = Color(0xFF6DCB5A)
                ) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(id = R.string.list_add_product))
                }
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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (displayedMembers.isEmpty()) {
                                MemberAvatar(
                                    name = stringResource(id = R.string.list_no_members),
                                    modifier = Modifier.size(40.dp)
                                )
                            } else {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    displayedMembers.forEach { member ->
                                        MemberAvatar(
                                            name = member.name,
                                            modifier = Modifier.size(40.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
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
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                uiState.description?.let { desc ->
                    if (desc.isNotBlank()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = stringResource(id = R.string.list_description_title),
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = desc,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF303F4F)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

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
                        IconButton(onClick = { showFilterDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(id = R.string.filters),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (uiState.items.isEmpty() && !uiState.isLoading) {
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
                                ,
                                isEditable = !isArchived
                            )
                        }
                    }
                }
            }

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

    if (showEditDialog && itemToEdit != null) {
        EditItemDialog(
            itemName = itemToEdit!!.name,
            quantity = itemToEdit!!.quantity?.toString() ?: "1",
            unit = itemToEdit!!.unit ?: "kg",
            onDismiss = {
                showEditDialog = false
                itemToEdit = null
            },
            onSave = { name, quantity, unit ->
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

    if (showFilterDialog) {
        FilterListItemsDialog(
            currentFilter = uiState.filter,
            categories = uiState.categories.map { it.id to it.name },
            onDismiss = { showFilterDialog = false },
            onApplyFilter = { filter ->
                viewModel.applyFilter(filter)
                showFilterDialog = false
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
    name: String,
    modifier: Modifier = Modifier
) {
    val initials = name.split(' ')
        .mapNotNull { it.firstOrNull()?.toString() }
        .joinToString("")

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(Color(0xFF6DCB5A)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials.uppercase(),
            style = MaterialTheme.typography.bodySmall,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}