package com.hci_listio_app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.hci_listio_app.ui.Components.ListioTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateListScreen(
    navController: NavController
) {
    var listName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            ListioTopAppBar(
                title = "Crear lista",
                showBackButton = true,
                onBackClick = { navController.navigateUp() }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "Nombre de la lista", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = listName,
                onValueChange = { listName = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Ej: Compras Semanales") }
            )

            Button(onClick = {
                // TODO: Crear la lista en el repositorio y obtener un id real.
                // Por ahora solo navegamos atrás.
                navController.navigateUp()
            }) {
                Text(text = "Crear")
            }

            TextButton(onClick = { navController.navigateUp() }) {
                Text(text = "Cancelar")
            }
        }
    }
}
