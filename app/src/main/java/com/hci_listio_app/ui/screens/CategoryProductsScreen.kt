package com.hci_listio_app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.hci_listio_app.R
import com.hci_listio_app.ui.Components.AddProductDialog
import com.hci_listio_app.ui.Components.ListioTopAppBar
import com.hci_listio_app.ui.viewmodels.CategoryProductsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun CategoryProductsScreen(
    navController: NavController,
    categoryName: String,
    categoryId: Long? = null,
    viewModel: CategoryProductsViewModel = viewModel()
) {
    // Set categoryId in ViewModel if provided
    LaunchedEffect(categoryId) {
        categoryId?.let { viewModel.setCategoryId(it) }
    }


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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.empty_list_icon),
                contentDescription = "Empty list",
                tint = Color(0xFF6DCB5A),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(text = "Empieza a agregar productos a la categoria")
            Text(
                text = "Tu categoria está vacía",
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (viewModel.errorMessage != null) {
                Text(
                    text = viewModel.errorMessage ?: "",
                    color = Color.Red,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            if (viewModel.isLoading) {
                CircularProgressIndicator(color = Color(0xFF6DCB5A))
            } else {
                Button(
                    onClick = { viewModel.onAddProductClicked() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6DCB5A))
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar productos")
                    Text("Agregar productos", modifier = Modifier.padding(start = 4.dp))
                }
            }
        }
    }

    if (viewModel.showDialog) {
        AddProductDialog(
            onDismiss = { viewModel.onDialogDismiss() },
            onSave = { productName ->
                viewModel.onProductSaved(productName)
            }
        )
    }
}