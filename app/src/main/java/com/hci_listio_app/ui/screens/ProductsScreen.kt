package com.hci_listio_app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.hci_listio_app.ui.Components.AddCategoriaCard
import com.hci_listio_app.ui.Components.BottomNavigationBar
import com.hci_listio_app.ui.Components.CategoriaCard
import com.hci_listio_app.ui.Components.ListioTopAppBar
import com.hci_listio_app.ui.viewmodels.ProductsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    navController: NavController,
    viewModel: ProductsViewModel = viewModel()
) {
    val categorias by viewModel.categorias.collectAsState()

    Scaffold(
        containerColor = Color(0xFFFAFAFA),
        topBar = { ListioTopAppBar(title = "Productos") },
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            // Campo de búsqueda
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Buscar producto por nombre") },
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
            Text("Buscar por categorías:", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            // Grid de categorías
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(4.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categorias) { categoria ->
                    CategoriaCard(categoria) {
                        navController.navigate("category/${categoria.nombre}")
                    }
                }

                item {
                    AddCategoriaCard(onClick = { /* TODO */ })
                }
            }
        }
    }
}
