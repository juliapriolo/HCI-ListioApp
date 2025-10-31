package com.hci_listio_app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hci_listio_app.ui.Screens.LoginScreen
import com.hci_listio_app.ui.Screens.SignUpScreen
import com.hci_listio_app.ui.Screens.ProductsScreen
import com.hci_listio_app.ui.Screens.ProfileScreen
import com.hci_listio_app.ui.Screens.HomeScreen
import com.hci_listio_app.ui.Screens.EditProfileScreen
import com.hci_listio_app.ui.Screens.LanguageScreen
import com.hci_listio_app.ui.screens.CategoryProductsScreen


@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(Screen.SignUp.route) {
            SignUpScreen(navController = navController)
        }
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.Products.route) {
            ProductsScreen(navController = navController)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }
        composable(Screen.EditProfile.route) {
            EditProfileScreen(navController = navController)
        }
        composable(Screen.Language.route) {
            LanguageScreen(navController = navController)
        }
        composable("category/{categoryName}") { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: ""
            CategoryProductsScreen(navController = navController, categoryName = categoryName)
        }

    }
}
