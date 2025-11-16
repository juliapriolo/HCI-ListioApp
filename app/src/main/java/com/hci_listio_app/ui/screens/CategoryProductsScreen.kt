package com.hci_listio_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.hci_listio_app.R
import com.hci_listio_app.ui.Components.AddProductDialog
import com.hci_listio_app.ui.Components.ListioTopAppBar
import com.hci_listio_app.ui.Components.ProductItem
import com.hci_listio_app.ui.Components.Categoria
import com.hci_listio_app.ui.Components.getCategoriaDisplayName
import com.hci_listio_app.ui.viewmodels.CategoryProductsViewModel
import com.hci_listio_app.ui.viewmodels.CategoryProductsViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryProductsScreen(
    navController: NavController,
    categoryName: String,
    categoryId: Long
) {

    val context = LocalContext.current
    val viewModel: CategoryProductsViewModel =
        viewModel(factory = CategoryProductsViewModelFactory(
            application = context.applicationContext as android.app.Application,
            categoryId = categoryId
        ))

    val uiState by viewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    val categoriaForDisplay = remember(categoryId, categoryName) {
        Categoria(id = categoryId, nombre = categoryName)
    }

    Scaffold(
        containerColor = Color(0xFFFAFAFA),
        topBar = {
            ListioTopAppBar(
                title = getCategoriaDisplayName(categoriaForDisplay),
                showBackButton = true,
                onBackClick = { navController.popBackStack() }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = Color.White,
                contentColor = Color(0xFF6DCB5A)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.category_products_add_icon),
                    tint = Color(0xFF6DCB5A)
                )
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {

            // ⭐ Header con título + botón redondo de agregar
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = getCategoriaDisplayName(categoriaForDisplay),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = { showDialog = true },
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFFF2F2F2), CircleShape) // gris suave
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = stringResource(R.string.category_products_add_icon),
                        tint = Color(0xFF6DCB5A) // verde del Figma
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            if (uiState.products.isEmpty() && !uiState.isLoading) {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 32.dp),
                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.empty_list_icon),
                            contentDescription = stringResource(R.string.category_products_empty_icon),
                            tint = Color(0xFF6DCB5A),
                            modifier = Modifier.size(70.dp)
                        )

                        Spacer(Modifier.height(12.dp))

                        Text(
                            text = stringResource(R.string.category_products_empty_subtitle),
                            color = Color.Gray,
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = stringResource(R.string.category_products_empty_instruction),
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            textAlign = TextAlign.Center
                        )

                    }
                }

            } else {


                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(uiState.products) { product ->
                        ProductItem(
                            productName = product.name,
                            brand = product.metadata?.get("brand") as? String,
                            onDelete = { viewModel.deleteProduct(product.id) }
                        )
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
