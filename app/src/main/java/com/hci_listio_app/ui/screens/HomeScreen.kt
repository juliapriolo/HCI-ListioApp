package com.hci_listio_app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.hci_listio_app.R
import com.hci_listio_app.ui.Components.BottomNavigationBar
import com.hci_listio_app.ui.Components.ListioTopAppBar
import com.hci_listio_app.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        containerColor = Color(0xFFFAFAFA),
        topBar = { ListioTopAppBar(title = stringResource(R.string.home_title)) },
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    navController.navigate(
                        Screen.ShoppingList.createRoute(1)
                    )
                }
            ) {
                Text(stringResource(R.string.home_view_list))
            }

            Text(stringResource(R.string.home_screen))
        }
    }
}