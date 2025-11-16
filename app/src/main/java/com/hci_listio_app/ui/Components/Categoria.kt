package com.hci_listio_app.ui.Components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.hci_listio_app.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
 
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.IconButton
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton

data class Categoria(
    val id: Long,
    val nombre: String,
    val imagenRes: Int = R.drawable.ic_categoria_default,
    val isDefault: Boolean = false
)
@Composable
fun CategoriaCard(
    categoria: Categoria,
    onClick: () -> Unit,
    onDelete: ((Categoria) -> Unit)? = null
) {
    var expanded by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onClick() }
    ) {
        Box {
            Image(
                painter = painterResource(id = categoria.imagenRes),
                contentDescription = categoria.nombre,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x66000000)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = getCategoriaDisplayName(categoria),
                    color = Color.White,
                    modifier = Modifier.padding(8.dp),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp
                )

                if (onDelete != null) {
                    Box(
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = stringResource(id = R.string.list_options), tint = Color.White)
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(id = R.string.category_products_menu_delete), color = Color.Red) },
                                onClick = {
                                    expanded = false
                                    showConfirm = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            title = { Text(stringResource(id = R.string.confirm_delete_category_title), color = Color.Black) },
            text = { Text(stringResource(id = R.string.confirm_delete_category_text)) },
            confirmButton = {
                TextButton(onClick = {
                    showConfirm = false
                    onDelete?.invoke(categoria)
                }) {
                    Text(stringResource(id = R.string.confirm_delete), color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirm = false }) {
                    Text(stringResource(id = R.string.common_cancel))
                }
            }
        )
    }
}


@Composable
fun AddCategoriaCard(onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFA5D6A7)),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                stringResource(id = R.string.add_category_text),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color.White
            )
        }
    }
}

@Composable
fun getCategoriaDisplayName(categoria: Categoria): String {
    fun Categoria.getTranslationKey(): Int? = when (nombre.trim().lowercase()) {
        "bebidas" -> R.string.category_name_bebidas
        "carnes y pescados" -> R.string.category_name_carnes
        "lácteos" -> R.string.category_name_lacteos
        "limpieza y hogar" -> R.string.category_name_limpieza
        "verdulería" -> R.string.category_name_verduleria
        "cuidado personal" -> R.string.category_name_cuidadopersonal
        "mascotas" -> R.string.category_name_mascotas
        "panadería" -> R.string.category_name_panaderia
        "snacks" -> R.string.category_name_snacks
        "congelados" -> R.string.category_name_congelados
        "despensa" -> R.string.category_name_despensa
        "bebés" -> R.string.category_name_bebes
        else -> null
    }
    val key = categoria.getTranslationKey()
    return if (key != null) stringResource(id = key) else categoria.nombre
}
