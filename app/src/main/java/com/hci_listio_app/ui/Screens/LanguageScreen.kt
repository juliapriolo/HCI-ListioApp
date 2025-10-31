package com.hci_listio_app.ui.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.hci_listio_app.ui.Components.ListioTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageScreen(navController: NavController) {
    val selected = remember { mutableStateOf("es") }

    Scaffold(
        containerColor = Color(0xFFFAFAFA),
        topBar = {
            ListioTopAppBar(
                title = "Idioma",
                showBackButton = true,
                onBackClick = { navController.navigateUp() }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .padding(32.dp)
            ) {
                Text(
                    text = "Selecciona Idioma",
                    fontSize = 32.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = Color(0xFF303F4F),
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }

            // Sección con padding 16.dp para igualar márgenes de configuración de perfil
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                // Card estilo lista de configuración de perfil
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column {
                        LanguageOption(
                            label = "Español",
                            selected = selected.value == "es",
                            onClick = { selected.value = "es" }
                        )
                        Divider(color = Color(0xFFE0E0E0))
                        LanguageOption(
                            label = "Inglés",
                            selected = selected.value == "en",
                            onClick = { selected.value = "en" }
                        )
                    }
                }

                Spacer(modifier = Modifier.size(8.dp))

                Button(
                    onClick = { navController.navigateUp() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6DCB5A)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(text = "Guardar", color = Color.White, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
private fun LanguageOption(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .height(56.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF333333)
        )
        RadioButton(selected = selected, onClick = onClick)
    }
}


