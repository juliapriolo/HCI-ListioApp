package com.hci_listio_app.ui.Screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.hci_listio_app.ui.Components.BottomNavigationBar
import com.hci_listio_app.ui.Components.ListioTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        containerColor = Color(0xFFFAFAFA), // Fondo gris muy claro
        topBar = { ListioTopAppBar(title = "Inicio") },
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text("Pantalla de Inicio")
        }
    }
}