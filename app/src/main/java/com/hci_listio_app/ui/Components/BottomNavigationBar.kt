package com.hci_listio_app.ui.Components

import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.hci_listio_app.ui.navigation.Screen

@Composable
fun BottomNavigationBar(navController: NavController) {
    val selectedIconColor = Color(0xFF689248)
    val unselectedIconColor = Color(0xFF9DB2CE)

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color.White,
        modifier = Modifier.height(70.dp)
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Productos") },
            selected = currentRoute == Screen.Products.route,
            onClick = { navController.navigate(Screen.Products.route) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = selectedIconColor,
                unselectedIconColor = unselectedIconColor,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
            selected = currentRoute == Screen.Home.route,
            onClick = { navController.navigate(Screen.Home.route) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = selectedIconColor,
                unselectedIconColor = unselectedIconColor,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
            selected = currentRoute == Screen.Profile.route,
            onClick = { navController.navigate(Screen.Profile.route) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = selectedIconColor,
                unselectedIconColor = unselectedIconColor,
                indicatorColor = Color.Transparent
            )
        )
    }
}