package com.hci_listio_app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.hci_listio_app.R
import com.hci_listio_app.data.remote.ProductRemoteDataSource
import com.hci_listio_app.data.repository.CategoryProductsRepository
import com.hci_listio_app.data.AuthRepositoryProvider
import com.hci_listio_app.ui.Components.AddProductDialog
import com.hci_listio_app.ui.Components.ListioTopAppBar
import com.hci_listio_app.ui.Components.ProductItem
import com.hci_listio_app.ui.Components.ConfirmDeleteDialog
import com.hci_listio_app.ui.viewmodels.CategoryProductsViewModel
import com.hci_listio_app.ui.viewmodels.CategoryProductsViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryProductsScreen(
    navController: NavController,
    categoryName: String,
    categoryId: Long? = null
) {
    // 🔐 Token del usuario
    val token = AuthRepositoryProvider.instance.authToken.value ?: ""

    // 🧠 ViewModel
    val viewModel: CategoryProductsViewModel = viewModel(
        factory = CategoryProductsViewModelFactory(
            repository = CategoryProductsRepository(ProductRemoteDataSource()),
            token = token,
            categoryId = categoryId ?: -1L
        )
    )

    val uiState by viewModel.uiState.collectAsState()

    // ➕ Diálogo agregar producto
    var showAddDialog by remember { mutableStateOf(false) }

    // ❌ Diálogo eliminar producto
    var showDeleteDialog by remember { mutableStateOf(false) }
    var productToDelete by remember { mutableStateOf<Long?>(null) }

    Scaffold(
        topBar = {
            ListioTopAppBar(
                title = categoryName,
                showBackButton = true,
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (uiState.products.isEmpty()) {
                Icon(
                    painter = painterResource(R.drawable.empty_list_icon),
                    contentDescription = "Empty list",
                    tint = Color(0xFF6DCB5A),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text("Empieza a agregar productos a la categoría")
                Text(
                    "Tu categoría está vacía",
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            } else {
                Text(
                    text = categoryName,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(bottom = 8.dp)
                )

                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(uiState.products) { product ->
                        ProductItem(
                            productName = product.name,
                            onDelete = {
                                productToDelete = product.id
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }

            // Errores
            uiState.error?.let { error ->
                Text(error, color = Color.Red)
            }

            // Loading
            if (uiState.isLoading) {
                CircularProgressIndicator(color = Color(0xFF6DCB5A))
            } else {
                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6DCB5A)),
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar productos")
                    Text("Agregar productos", modifier = Modifier.padding(start = 4.dp))
                }
            }
        }
    }

    // ➕ Diálogo agregar producto
    if (showAddDialog) {
        AddProductDialog(
            onDismiss = { showAddDialog = false },
            onSave = { productName ->
                viewModel.addProduct(productName)
                showAddDialog = false
            }
        )
    }

    // ❌ Diálogo eliminar producto
    if (showDeleteDialog && productToDelete != null) {
        ConfirmDeleteDialog(
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                viewModel.deleteProduct(productToDelete!!)
                showDeleteDialog = false
            }
        )
    }
}
