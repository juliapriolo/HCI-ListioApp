package com.hci_listio_app.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Products : Screen("products")
    object Profile : Screen("profile")
}