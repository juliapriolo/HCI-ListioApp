package com.hci_listio_app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.hci_listio_app.R
import com.hci_listio_app.data.AuthRepositoryProvider
import com.hci_listio_app.ui.Components.*
import com.hci_listio_app.ui.viewmodels.ProductsViewModel
import com.hci_listio_app.ui.viewmodels.ProductsViewModelFactory
import com.hci_listio_app.data.repository.DefaultCategoriesInitializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(navController: NavController) {

    val token = AuthRepositoryProvider.instance.authToken.value ?: ""
    val viewModel: ProductsViewModel =
        viewModel(factory = ProductsViewModelFactory(token))

    val categorias by viewModel.categorias.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var showAddCategory by remember { mutableStateOf(false) }

    val initialized = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(token) {
        if (!initialized.value && token.isNotBlank()) {
            scope.launch(Dispatchers.IO) {
                try {
                    DefaultCategoriesInitializer().loadOrCreate(token)
                    viewModel.loadCategories()
                } catch (_: Exception) {}
                initialized.value = true
            }
        }
    }

    // ---- GRID COLUMNS SEGÚN ANCHO DE PANTALLA ----
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp
    // breakpoints aproximados: phone, tablet chica, tablet grande / iPad
    val gridColumns = when {
        screenWidthDp >= 840 -> 4  // tablet grande / iPad horizontal
        screenWidthDp >= 600 -> 3  // tablet chica / iPad vertical
        else -> 2                  // teléfono
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { ListioTopAppBar(title = stringResource(R.string.products_title)) },
        bottomBar = { BottomNavigationBar(navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddCategory = true },
                containerColor = Color.White,
                contentColor = Color(0xFF6DCB5A)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.products_add_category)
                )
            }
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {

                LazyVerticalGrid(
                    columns = GridCells.Fixed(gridColumns),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    // ==== SEARCH BAR ====
                    item(span = { GridItemSpan(gridColumns) }) {
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

                    if (searchQuery.isBlank()) {

                        // ==== TÍTULO ====
                        item(span = { GridItemSpan(gridColumns) }) {
                            Text(
                                text = stringResource(R.string.products_search_categories),
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        // ==== CATEGORÍAS ====
                        items(categorias) { categoria ->
                            CategoriaCard(
                                categoria = categoria,
                                onClick = {
                                    navController.navigate(
                                        "category/${categoria.nombre}?categoryId=${categoria.id}"
                                    )
                                },
                                onDelete = { c -> viewModel.deleteCategory(c) }
                            )
                        }

                    } else {

                        val showNoResults =
                            searchQuery.isNotBlank() && searchResults.isEmpty()

                        if (showNoResults) {

                            // ==== SIN RESULTADOS ====
                            item(span = { GridItemSpan(gridColumns) }) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 64.dp)
                                ) {
                                    Text(
                                        text = stringResource(R.string.products_no_results),
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color.Gray,
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }
                            }
                        } else {

                            // ==== RESULTADOS ====
                            items(searchResults, span = { GridItemSpan(gridColumns) }) { product ->
                                val brand = product.metadata?.get("brand") as? String
                                ProductItem(
                                    productName = product.name,
                                    brand = brand,
                                    onDelete = { /* implementar */ }
                                )
                            }
                        }
                    }
                }
            }
        }

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
}
