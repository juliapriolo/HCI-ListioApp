package com.hci_listio_app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
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
import com.hci_listio_app.ui.Components.AddCategoriaCard
import com.hci_listio_app.ui.Components.BottomNavigationBar
import com.hci_listio_app.ui.Components.CategoriaCard
import com.hci_listio_app.ui.Components.ListioTopAppBar
import com.hci_listio_app.ui.viewmodels.ProductsViewModel
import com.hci_listio_app.ui.viewmodels.ProductsViewModelFactory
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(navController: NavController) {

    val token = AuthRepositoryProvider.instance.authToken.value ?: ""

    val viewModel: ProductsViewModel = viewModel(
        factory = ProductsViewModelFactory(token)
    )

    val categorias by viewModel.categorias.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()

    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        containerColor = Color(0xFFFAFAFA),
        topBar = { ListioTopAppBar(title = stringResource(R.string.products_title)) },
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

            // 🔍 BUSCADOR
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

            Spacer(Modifier.height(16.dp))

            // ⭐ Si NO hay búsqueda → mostrar categorías
            if (searchQuery.isBlank()) {

                Text(
                    stringResource(R.string.products_search_categories),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(8.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(4.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    items(categorias) { categoria ->
                        CategoriaCard(categoria) {
                            navController.navigate(
                                "category/${categoria.nombre}?categoryId=${categoria.id}"
                            )
                        }
                    }

                    item {
                        AddCategoriaCard(onClick = { })
                    }
                }
            } else {

                // ⭐ Si hay texto en el buscador → mostrar productos filtrados
                Text(
                    "Resultados de búsqueda",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(searchResults) { product ->

                        // Reutilizamos ProductItem o un item simple
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(Color.White),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(product.name, style = MaterialTheme.typography.bodyLarge)
                                Text("Categoría: ${product.categoryName}", color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }
}
