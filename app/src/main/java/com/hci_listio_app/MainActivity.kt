package com.hci_listio_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.hci_listio_app.ui.navigation.AppNavigation
import com.hci_listio_app.ui.theme.HCIListioAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Se elimina enableEdgeToEdge() para resolver el fallo de inicio (pantalla negra).
        setContent {
            HCIListioAppTheme {
                AppNavigation()
            }
        }
    }
}