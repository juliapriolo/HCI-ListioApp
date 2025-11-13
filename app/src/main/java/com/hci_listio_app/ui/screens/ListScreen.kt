package com.hci_listio_app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.hci_listio_app.R
import com.hci_listio_app.ui.Components.ListItem
import com.hci_listio_app.ui.Components.ListItemData
import com.hci_listio_app.ui.Components.ListioTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(navController: NavController, listName: String = "Compras Cumpleaños") {
    // Estado de los items de la lista
    var items by remember {
        mutableStateOf(
            listOf(
                ListItemData("1", "Servilletas", isChecked = true),
                ListItemData("2", "Globos", isChecked = false),
                ListItemData("3", "Hamburguesas - 24 unidades", isChecked = false),
                ListItemData("4", "Queso - 500 gramos", isChecked = false),
                ListItemData("5", "Coca Cola - 10 Botellas de 2,25", isChecked = false),
                ListItemData("6", "Cebollas - 3 unidades", isChecked = false),
                ListItemData("7", "Helado - 5 kg", isChecked = false)
            )
        )
    }

    val completedCount = items.count { it.isChecked }
    val totalCount = items.size

    Scaffold(
        containerColor = Color(0xFFFAFAFA),
        topBar = {
            ListioTopAppBar(
                title = listName,
                showBackButton = true,
                onBackClick = { navController.navigateUp() }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Add new item */ },
                containerColor = Color(0xFF6DCB5A),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar producto")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Header con avatares y estado
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Avatares de usuarios
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Avatar 1
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF6DCB5A)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.perfilpredeterminado),
                                contentDescription = "Usuario 1",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Avatar 2
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF6DCB5A)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.perfilpredeterminado),
                                contentDescription = "Usuario 2",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Botón añadir usuario
                        IconButton(
                            onClick = { /* TODO: Add user */ },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE0E0E0))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Añadir usuario",
                                tint = Color.Gray
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // Botón filtro (menú de tres puntos en la imagen)
                        IconButton(onClick = { /* TODO: Filter options */ }) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Opciones",
                                tint = Color.Gray
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Estado de la lista y filtro
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.empty_list_icon),
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Lista $completedCount/$totalCount Completada",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Todos",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Lista de items
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(items) { item ->
                    ListItem(
                        item = item,
                        onCheckedChange = { checked ->
                            items = items.map {
                                if (it.id == item.id) it.copy(isChecked = checked) else it
                            }
                        },
                        onMoreClick = {
                            // TODO: Edit item
                        }
                    )
                }

                // Botón agregar más productos al final
                item {
                    Button(
                        onClick = { /* TODO: Add product */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6DCB5A)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Agregar más productos")
                    }
                }
            }
        }
    }
}
