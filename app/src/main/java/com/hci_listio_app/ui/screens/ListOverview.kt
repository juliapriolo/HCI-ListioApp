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
    val tabs = listOf("Personal", "Compartidas", "Historial")
    val selected = remember { mutableStateOf(0) }
    val showCreateDialog = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = rememberAppSnackbarHostState()
    val lifecycleOwner = LocalLifecycleOwner.current
    var listBeingEdited by remember { mutableStateOf<Pair<Long, String>?>(null) }
    var listBeingDeleted by remember { mutableStateOf<Pair<Long, String>?>(null) }

    // Reload lists whenever the screen comes back to the foreground
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadLists()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
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
                Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar")
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

                val filteredLists = uiState.lists
                    .filter { list ->
                        query.value.isBlank() || list.name.contains(query.value, ignoreCase = true)
                    }
                    .filter { list ->
                        when (selected.value) {
                            1 -> (list.sharedWith.isNotEmpty())
                            2 -> list.lastPurchasedAt != null || list.items.any { it.purchased }
                            else -> list.sharedWith.isEmpty()
                        }
                    }

                if (filteredLists.isEmpty() && !uiState.isLoading) {
                    EmptyState(modifier = Modifier.fillMaxWidth())
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        filteredLists.forEach { list ->
                            val sharedMembers = if (list.users.isNotEmpty()) list.users else list.sharedWith
                            OverviewCard(
                                item = OverviewItem(
                                    id = list.id.toString(),
                                    title = list.name,
                                    isPrivate = true,
                                    completed = list.items.count { it.purchased },
                                    total = list.items.size,
                                    members = sharedMembers.map { "${it.name} ${it.surname}" },
                                    isFavorite = uiState.favorites.contains(list.id)
                                ),
                                onClick = {
                                    navController.navigate(
                                        com.hci_listio_app.ui.navigation.Screen.ShoppingList.createRoute(list.id)
                                    )
                                },
                                onToggleFavorite = { viewModel.toggleFavorite(list.id) },
                                onEdit = { listBeingEdited = list.id to list.name },
                                onDelete = { listBeingDeleted = list.id to list.name },
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
            title = "Crear lista",
            nameLabel = "Nombre de la lista",
            showDescription = true,  //
            descriptionLabel = "Descripción",
            onDismiss = { showCreateDialog.value = false },
            onCreate = { name, description ->  //
                var createdId: Long? = null
                try {
                    // Ahora pasá también description
                    val result = ListRepositoryProvider.instance.createList(
                        name,
                        description ?: ""  // Pasar description o string vacío
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
                        snackbarHostState.showToast("Lista creada")
                        // Refrescar las listas en el overview para que la nueva aparezca inmediatamente
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
                                    if (success) "Lista actualizada" else message ?: "No se pudo actualizar la lista."
                                )
                            }
                        }
                        listBeingEdited = null
                    }
                }) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(onClick = { listBeingEdited = null }) { Text("Cancelar") }
            },
            title = { Text("Editar lista") },
            text = {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    singleLine = true,
                    label = { Text("Nombre") }
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
                    viewModel.deleteList(id) { success, message ->
                        coroutineScope.launch {
                            snackbarHostState.showToast(
                                if (success) "Lista eliminada" else message ?: "No se pudo eliminar la lista."
                            )
                        }
                    }
                    listBeingDeleted = null
                }) { Text("Eliminar", color = Color(0xFFD32F2F)) }
            },
            dismissButton = {
                TextButton(onClick = { listBeingDeleted = null }) { Text("Cancelar") }
            },
            title = { Text("Eliminar lista") },
            text = { Text("¿Seguro que deseas eliminar \"$name\"? Esta acción no se puede deshacer.") }
        )
    }
}