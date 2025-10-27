package com.hci_listio_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.hci_listio_app.ui.Screens.ProductsScreen
import com.hci_listio_app.ui.theme.HCIListioAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HCIListioAppTheme {
                ProductsScreen()
            }
        }
    }
}