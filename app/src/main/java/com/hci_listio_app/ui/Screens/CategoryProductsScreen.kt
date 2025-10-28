package com.hci_listio_app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.hci_listio_app.R
import com.hci_listio_app.ui.Components.ListioTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryProductsScreen(navController: NavController, categoryName: String) {
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
                painter = painterResource(R.drawable.empty_list_icon), // reemplazá con tu ícono
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

            Button(
                onClick = { /* abrir modal o acción futura */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6DCB5A))
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar productos")
                Text("Agregar productos", modifier = Modifier.padding(start = 4.dp))
            }
        }
    }
}