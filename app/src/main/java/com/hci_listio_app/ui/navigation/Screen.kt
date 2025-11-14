package com.hci_listio_app.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object VerifyAccount : Screen("verify_account/{email}/{password}") {
        fun createRoute(email: String, password: String = ""): String {
            val encodedEmail = java.net.URLEncoder.encode(email, "UTF-8")
            val encodedPassword = java.net.URLEncoder.encode(password, "UTF-8")
            return "verify_account/$encodedEmail/$encodedPassword"
        }
    }
    object ForgotPassword : Screen("forgot_password")
    object Home : Screen("home")
    object Products : Screen("products")
    object Profile : Screen("profile")
    object EditProfile : Screen("edit_profile")
    object Language : Screen("language")
    object ListOverview : Screen("list_overview")
    object ShoppingList : Screen("list/{listId}?originTab={originTab}") {
        fun createRoute(listId: Long, originTab: Int? = null): String {
            val tab = originTab ?: -1
            return "list/$listId?originTab=$tab"
        }
    }
}