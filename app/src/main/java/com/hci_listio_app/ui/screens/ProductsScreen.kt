package com.hci_listio_app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.hci_listio_app.R
import com.hci_listio_app.data.AuthRepositoryProvider
import com.hci_listio_app.ui.Components.*
import com.hci_listio_app.ui.viewmodels.ProductsViewModel
import com.hci_listio_app.ui.viewmodels.ProductsViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable


fun ProductsScreen(navController: NavController) {

    val token = AuthRepositoryProvider.instance.authToken.value ?: ""
    val viewModel: ProductsViewModel = viewModel(factory = ProductsViewModelFactory(token))

    val categorias by viewModel.categorias.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var showAddCategory by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var categoriaToDelete by remember { mutableStateOf<Categoria?>(null) }
    var isDeleting by remember { mutableStateOf(false) }
    var deleteError by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        containerColor = Color(0xFFFAFAFA),
        topBar = { ListioTopAppBar(title = stringResource(R.string.products_title)) },
        bottomBar = { BottomNavigationBar(navController) }
    ) { padding ->

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // 🔍 Buscador (Ocupando las 2 columnas)
            item(span = { GridItemSpan(2) }) { 
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        viewModel.searchProducts(it)
                    },
                    placeholder = { Text(stringResource(R.string.products_search)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF0F1F2),
                        unfocusedContainerColor = Color(0xFFF0F1F2),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }

            // ⭐ CATEGORÍAS (Solo si la búsqueda está vacía)
            if (searchQuery.isBlank()) {

                item(span = { GridItemSpan(2) }) {
                    Text(
                        text = stringResource(R.string.products_search_categories),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(categorias) { categoria ->
                    CategoriaCard(
                        categoria = categoria,
                        onClick = {
                            navController.navigate(
                                "category/${categoria.nombre}?categoryId=${categoria.id}"
                            )
                        },
                        onDelete = {
                            categoriaToDelete = categoria
                            showDeleteDialog = true
                        }
                    )
                }

                item {
                    AddCategoriaCard { showAddCategory = true }
                }
            }

            // ⭐ RESULTADOS DE BÚSQUEDA
            else {

                item(span = { GridItemSpan(2) }) {
                     Text(
                        text = "Resultados de búsqueda",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(searchResults.size, span = { GridItemSpan(2) }) { index ->
                    val product = searchResults[index]

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(product.name)
                            Text(
                                text = "Categoría: ${product.categoryName}",
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }

    // Diálogo para eliminar categoría
    if (showDeleteDialog && categoriaToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false; categoriaToDelete = null; deleteError = null },
            title = { Text("Eliminar categoría") },
            text = { Text("¿Estás seguro que deseas eliminar la categoría '${categoriaToDelete?.nombre}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        isDeleting = true
                        deleteError = null
                        coroutineScope.launch {
                            try {
                                val repo = viewModel.categoryRepo
                                val result = repo.deleteCategory(token, categoriaToDelete!!.id)
                                if (result.isSuccess) {
                                    showDeleteDialog = false
                                    categoriaToDelete = null
                                    viewModel.loadCategories()
                                } else {
                                    deleteError = result.exceptionOrNull()?.message ?: "Error al eliminar"
                                }
                            } catch (e: Exception) {
                                deleteError = e.message ?: "Error al eliminar"
                            } finally {
                                isDeleting = false
                            }
                        }
                    },
                    enabled = !isDeleting
                ) {
                    if (isDeleting) CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    Text("Eliminar", color = Color.Red, modifier = Modifier.padding(start = 4.dp))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false; categoriaToDelete = null; deleteError = null }) {
                    Text("Cancelar")
                }
            }
        )
        deleteError?.let {
            Text(it, color = Color.Red, modifier = Modifier.padding(16.dp))
        }
    }

    // ➕ DIALOGO NUEVA CATEGORIA
    if (showAddCategory) {
        AddCategoryDialog(
            onDismiss = { showAddCategory = false },
            onSave = {
                viewModel.createCategory(it)
                showAddCategory = false
            }
        )
    }
}
