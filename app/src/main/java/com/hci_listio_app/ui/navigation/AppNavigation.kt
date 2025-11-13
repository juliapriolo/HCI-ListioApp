package com.hci_listio_app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hci_listio_app.ui.screens.LoginScreen
import com.hci_listio_app.ui.screens.SignUpScreen
import com.hci_listio_app.ui.screens.VerifyAccountScreen
import com.hci_listio_app.ui.screens.ForgotPasswordScreen
import com.hci_listio_app.ui.screens.ProductsScreen
import com.hci_listio_app.ui.screens.ProfileScreen
import com.hci_listio_app.ui.screens.HomeScreen
import com.hci_listio_app.ui.screens.EditProfileScreen
import com.hci_listio_app.ui.screens.LanguageScreen
import com.hci_listio_app.ui.screens.ListScreen
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
        composable(
            route = Screen.VerifyAccount.route,
            arguments = listOf(
                navArgument("email") {
                    type = NavType.StringType
                },
                navArgument("password") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val emailArg = backStackEntry.arguments?.getString("email") ?: ""
            val passwordArg = backStackEntry.arguments?.getString("password") ?: ""
            val email = try {
                java.net.URLDecoder.decode(emailArg, "UTF-8")
            } catch (e: Exception) {
                emailArg
            }
            val password = try {
                java.net.URLDecoder.decode(passwordArg, "UTF-8")
            } catch (e: Exception) {
                passwordArg
            }
            VerifyAccountScreen(
                navController = navController,
                email = email,
                password = password
            )
        }
        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(navController = navController)
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
        composable(Screen.ShoppingList.route) { backStackEntry ->
            val listName = backStackEntry.arguments?.getString("listName") ?: "Mi Lista"
            ListScreen(navController = navController, listName = listName)
        }
    }
}