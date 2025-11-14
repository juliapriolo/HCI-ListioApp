package com.hci_listio_app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hci_listio_app.ui.screens.*

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {

        composable(Screen.Login.route) {
            LoginScreen(navController)
        }

        composable(Screen.SignUp.route) {
            SignUpScreen(navController)
        }

        composable(
            route = Screen.VerifyAccount.route,
            arguments = listOf(
                navArgument("email") { type = NavType.StringType },
                navArgument("password") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val password = backStackEntry.arguments?.getString("password") ?: ""
            VerifyAccountScreen(navController, email, password)
        }

        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(navController)
        }

        composable(Screen.Home.route) {
            HomeScreen(navController)
        }

        composable(Screen.Products.route) {
            ProductsScreen(navController)
        }

        composable(Screen.Profile.route) {
            ProfileScreen(navController)
        }

        composable(Screen.EditProfile.route) {
            EditProfileScreen(navController)
        }

        composable(Screen.Language.route) {
            LanguageScreen(navController)
        }

        composable(Screen.ListOverview.route) {
            ListOverview(navController)
        }

        // ------------------------
        // ⭐ CATEGORY PRODUCTS
        // ------------------------
        composable(
            route = "category/{categoryName}?categoryId={categoryId}",
            arguments = listOf(
                navArgument("categoryName") { type = NavType.StringType },
                navArgument("categoryId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: ""
            val categoryId = backStackEntry.arguments?.getLong("categoryId") ?: -1L

            CategoryProductsScreen(
                navController = navController,
                categoryName = categoryName,
                categoryId = categoryId
            )
        }

        composable(
            route = Screen.ShoppingList.route,
            arguments = listOf(
                navArgument("listId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val listId = backStackEntry.arguments?.getLong("listId") ?: 1L
            ListScreen(navController, listId)
        }
    }
}
