package com.hci_listio_app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(navController: NavController) {

    val token = AuthRepositoryProvider.instance.authToken.value ?: ""
    val viewModel: ProductsViewModel = viewModel(factory = ProductsViewModelFactory(token))

    val categorias by viewModel.categorias.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var showAddCategory by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
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

            // 🔍 BUSCADOR
            item(span = { GridItemSpan(2) }) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = {
                            searchQuery = it
                            viewModel.searchProducts(it)
                        },
                        placeholder = stringResource(R.string.products_search),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // ⭐ SIN BUSQUEDA → Mostrar categorías
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
                        onDelete = if (categoria.isDefault) null else { c -> /* lógica de borrado */ }
                    )
                }
                item {
                    AddCategoriaCard { showAddCategory = true }
                }
            }

            // ⭐ CON BUSQUEDA → mostrar productos encontrados
            else {
                val showNoResults = searchQuery.isNotBlank() && searchResults.isEmpty()
                if (showNoResults) {
                    item(span = { GridItemSpan(2) }) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 64.dp)
                        ) {
                            Text(
                                text = "No se encontraron productos",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.Gray,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                } else {
                    items(searchResults, span = { GridItemSpan(2) }) { product ->
                        val brand = product.metadata?.get("brand") as? String
                        ProductItem(
                            productName = product.name,
                            brand = brand,
                            onDelete = { /* lógica de borrado */ }
                        )
                    }
                }
            }
        }
    }

    // Diálogo para crear nueva categoría
    if (showAddCategory) {
        AddCategoryDialog(
            onDismiss = { showAddCategory = false },
            onSave = { name ->
                viewModel.createCategory(name)
                showAddCategory = false
            }
        )
    }
}