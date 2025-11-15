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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.hci_listio_app.R
import com.hci_listio_app.ui.Components.AddProductDialog
import com.hci_listio_app.ui.Components.ListioTopAppBar
import com.hci_listio_app.ui.Components.ProductItem
import com.hci_listio_app.ui.viewmodels.CategoryProductsViewModel
import com.hci_listio_app.ui.viewmodels.CategoryProductsViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryProductsScreen(
    navController: NavController,
    categoryName: String,
    categoryId: Long
) {
    // La Vista ya no se preocupa por el token. La fábrica se encarga de todo.
    val viewModel: CategoryProductsViewModel = viewModel(
        factory = CategoryProductsViewModelFactory(categoryId)
    )

    val uiState by viewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color(0xFFFAFAFA),
        topBar = {
            ListioTopAppBar(
                title = categoryName,
                showBackButton = true,
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { padding ->
        if (uiState.products.isEmpty() && !uiState.isLoading) {
            // Estado vacío
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.empty_list_icon),
                    contentDescription = "Lista vacía",
                    tint = Color(0xFF6DCB5A),
                    modifier = Modifier.padding(bottom = 8.dp).size(64.dp)
                )
                Text(
                    text = "Tu categoría está vacía",
                    color = Color.Gray,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Empieza a agregar productos a la categoría", 
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Button(
                    onClick = { showDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6DCB5A))
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Text("Agregar productos", modifier = Modifier.padding(start = 4.dp))
                }
            }
        } else {
            // Lista de productos
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                     Text(
                        text = categoryName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp, top = 8.dp)
                    )
                }
                items(uiState.products) { product ->
                    ProductItem(
                        productName = product.name,
                        brand = product.metadata?.get("brand"),
                        onDelete = { viewModel.deleteProduct(product.id) }
                    )
                }
                item {
                    Button(
                        onClick = { showDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6DCB5A))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Agregar más productos", color = Color.White)
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AddProductDialog(
            onDismiss = { showDialog = false },
            onSave = { name, brand ->
                viewModel.addProduct(name, brand)
                showDialog = false
            }
        )
    }
}