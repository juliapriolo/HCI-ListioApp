package com.hci_listio_app.ui.Components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun BottomNavigationBar() {
    val selectedIconColor = Color(0xFF689248)
    val unselectedIconColor = Color(0xFF9DB2CE)

    NavigationBar(containerColor = Color.White) {
        // Asumimos que "Productos" es la pantalla actual para el propósito de este ejemplo
        val isProductsSelected = true 

        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
            label = { Text("Inicio") },
            selected = !isProductsSelected,
            onClick = { /* TODO: Navegar a Inicio */ },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = selectedIconColor,
                unselectedIconColor = unselectedIconColor,
                indicatorColor = Color.Transparent // Opcional: para quitar el fondo del ícono seleccionado
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Productos") },
            label = { Text("Productos") },
            selected = isProductsSelected,
            onClick = { /* No hacer nada si ya estamos aquí */ },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = selectedIconColor,
                unselectedIconColor = unselectedIconColor,
                indicatorColor = Color.Transparent
            )
        )
    }
}