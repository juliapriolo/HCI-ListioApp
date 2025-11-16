package com.hci_listio_app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.hci_listio_app.R
import com.hci_listio_app.ui.Components.*
import com.hci_listio_app.ui.viewmodels.ListOverviewViewModel
import com.hci_listio_app.data.ListRepositoryProvider
import kotlinx.coroutines.launch

@Composable
fun ListOverview(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: ListOverviewViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val query = remember { mutableStateOf("") }
    val tabs = listOf(
        stringResource(id = R.string.tab_personal),
        stringResource(id = R.string.tab_shared),
        stringResource(id = R.string.tab_history)
    )
    val selected = remember { mutableStateOf(0) }
    val showCreateDialog = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = rememberAppSnackbarHostState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var listBeingEdited by remember { mutableStateOf<Pair<Long, String>?>(null) }
    var listBeingDeleted by remember { mutableStateOf<Pair<Long, String>?>(null) }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadLists()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    val restoredTabFlow = remember(savedStateHandle) {
        savedStateHandle?.getStateFlow("overviewTab", -1)
    }
    val restoredTab = restoredTabFlow?.collectAsState()?.value

    LaunchedEffect(restoredTab) {
        if (restoredTab != null && restoredTab >= 0 && restoredTab != selected.value) {
            selected.value = restoredTab
            savedStateHandle?.remove<Int>("overviewTab")
        }
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
        topBar = { ListioTopAppBar(title = stringResource(id = R.string.lists_title)) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog.value = true },
                containerColor = Color.White,
                contentColor = Color(0xFF6DCB5A)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(id = R.string.create_list))
            }
        },
        bottomBar = { BottomNavigationBar(navController = navController) },
        snackbarHost = { AppSnackbarHost(hostState = snackbarHostState) },
        modifier = modifier
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                SearchBar(
                    query = query.value,
                    onQueryChange = { query.value = it },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                ListsTabs(
                    tabs = tabs,
                    selectedIndex = selected.value,
                    onSelectedChange = { selected.value = it },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                val archivedIds = uiState.archivedListIds
                val filteredLists = uiState.lists
                    .filter { list ->
                        query.value.isBlank() || list.name.contains(query.value, ignoreCase = true)
                    }
                    .filter { list ->
                        val sharedMembers = if (list.users.isNotEmpty()) list.users else list.sharedWith
                        val isShared = sharedMembers.isNotEmpty()
                        val isArchived = archivedIds.contains(list.id)
                        when (selected.value) {
                            1 -> !isArchived && isShared
                            2 -> isArchived
                            else -> !isArchived && !isShared
                        }
                    }

                if (filteredLists.isEmpty() && !uiState.isLoading) {
                    EmptyState(modifier = Modifier.fillMaxWidth())
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        filteredLists.forEach { list ->
                            val sharedMembers = if (list.users.isNotEmpty()) list.users else list.sharedWith
                            val isShared = sharedMembers.isNotEmpty()
                            val isArchived = archivedIds.contains(list.id)

                            OverviewCard(
                                item = OverviewItem(
                                    id = list.id.toString(),
                                    title = list.name,
                                    isPrivate = !isShared,
                                    completed = list.items.count { it.purchased },
                                    total = list.items.size,
                                    members = sharedMembers.map { "${it.name} ${it.surname}" },
                                    isFavorite = uiState.favorites.contains(list.id)
                                ),
                                onClick = {
                                    navController.navigate(
                                        com.hci_listio_app.ui.navigation.Screen.ShoppingList.createRoute(
                                            list.id,
                                            selected.value
                                        )
                                    )
                                },
                                onToggleFavorite = { viewModel.toggleFavorite(list.id) },
                                onEdit = if (!isArchived) { { listBeingEdited = list.id to list.name } } else null,
                                onDelete = { listBeingDeleted = list.id to list.name },
                                onRestore = if (isArchived) {
                                    {
                                        viewModel.restoreList(list.id)
                                                coroutineScope.launch {
                                                    snackbarHostState.showToast(context.getString(R.string.list_restored))
                                                }
                                    }
                                } else null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
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

    // Diálogo para crear lista
    if (showCreateDialog.value) {
        CreateEntityDialog(
            title = stringResource(id = R.string.create_list),
            nameLabel = stringResource(id = R.string.create_list_name),
            showDescription = true,
            descriptionLabel = stringResource(id = R.string.create_list_description),
            onDismiss = { showCreateDialog.value = false },
            onCreate = { name, description ->
                var createdId: Long? = null
                try {
                    val result = ListRepositoryProvider.instance.createList(
                        name,
                        description ?: ""
                    )
                    if (result.isSuccess) {
                        val response = result.getOrNull()
                        createdId = response?.id
                    } else {
                        throw result.exceptionOrNull() ?: Exception("No se pudo crear. Intenta nuevamente.")
                    }
                } catch (e: Exception) {
                    throw e
                }

                if (createdId != null) {
                    coroutineScope.launch {
                        snackbarHostState.showToast(context.getString(R.string.list_created))
                        viewModel.loadLists()
                    }
                }

                createdId
            }
        )
    }

    if (listBeingEdited != null) {
        val (id, originalName) = listBeingEdited!!
        var newName by remember(listBeingEdited) { mutableStateOf(originalName) }
        AlertDialog(
            onDismissRequest = { listBeingEdited = null },
            confirmButton = {
                TextButton(onClick = {
                    if (newName.isNotBlank()) {
                        viewModel.renameList(id, newName.trim()) { success, message ->
                            coroutineScope.launch {
                                snackbarHostState.showToast(
                                    if (success) context.getString(R.string.list_updated) else message ?: context.getString(R.string.list_not_updated)
                                )
                            }
                        }
                        listBeingEdited = null
                    }
                }) { Text(stringResource(id = R.string.common_save)) }
            },
            dismissButton = {
                TextButton(onClick = { listBeingEdited = null }) { Text(stringResource(id = R.string.common_cancel)) }
            },
            title = { Text(stringResource(id = R.string.edit_list_title)) },
            text = {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    singleLine = true,
                    label = { Text(stringResource(id = R.string.create_list_name)) }
                )
            }
        )
    }

    if (listBeingDeleted != null) {
        val (id, name) = listBeingDeleted!!
        AlertDialog(
            onDismissRequest = { listBeingDeleted = null },
            confirmButton = {
                TextButton(onClick = {
                    val deletingHistory = selected.value == 2
                    viewModel.deleteList(id, deletingHistory) { success, message ->
                        coroutineScope.launch {
                            val defaultMessage = if (deletingHistory) {
                                if (success) context.getString(R.string.list_deleted) else context.getString(R.string.list_not_deleted)
                            } else {
                                if (success) context.getString(R.string.list_moved_to_history) else context.getString(R.string.list_not_moved_to_history)
                            }
                            snackbarHostState.showToast(message ?: defaultMessage)
                        }
                    }
                    listBeingDeleted = null
                }) {
                    val deletingHistory = selected.value == 2
                    Text(
                        if (deletingHistory) stringResource(id = R.string.confirm_delete) else stringResource(id = R.string.move_to_history),
                        color = if (deletingHistory) Color(0xFFD32F2F) else Color(0xFF6DCB5A)
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { listBeingDeleted = null }) { Text(stringResource(id = R.string.common_cancel)) }
            },
            title = { Text(stringResource(id = R.string.delete_list_title)) },
            text = { Text(stringResource(id = R.string.delete_list_confirmation, name)) }
        )
    }
}