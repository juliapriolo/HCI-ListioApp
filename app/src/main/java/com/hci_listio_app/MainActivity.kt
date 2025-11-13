package com.hci_listio_app

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.hci_listio_app.data.LanguagePreferences
import com.hci_listio_app.ui.navigation.AppNavigation
import com.hci_listio_app.ui.theme.HCIListioAppTheme
import com.hci_listio_app.utils.LocaleHelper
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    override fun attachBaseContext(newBase: Context?) {
        if (newBase != null) {
            // Leer la preferencia guardada de forma síncrona usando el contexto de la aplicación
            val appContext = newBase.applicationContext
            val preferences = LanguagePreferences(appContext)
            val savedLanguage = runBlocking {
                try {
                    preferences.getLanguageCode()
                } catch (e: Exception) {
                    null
                }
            }
            
            // Usar el idioma guardado o el del sistema
            val languageCode = savedLanguage ?: LocaleHelper.getSystemLanguage(newBase)
            
            // Aplicar el locale al contexto base
            val locale = java.util.Locale(languageCode)
            java.util.Locale.setDefault(locale)
            
            val config = android.content.res.Configuration(newBase.resources.configuration)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                config.setLocale(locale)
                val contextWithLocale = newBase.createConfigurationContext(config)
                super.attachBaseContext(contextWithLocale)
            } else {
                @Suppress("DEPRECATION")
                config.locale = locale
                @Suppress("DEPRECATION")
                newBase.resources.updateConfiguration(config, newBase.resources.displayMetrics)
                super.attachBaseContext(newBase)
            }
        } else {
            super.attachBaseContext(newBase)
        }
    }
    
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