package com.hci_listio_app.ui.Components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.hci_listio_app.R

data class SharedUser(
    val id: Long,
    val name: String,
    val role: String, // "Creador" o "Editor"
    val photoRes: Int = R.drawable.perfilpredeterminado
)

@Composable
fun ShareListDialog(
    currentUsers: List<SharedUser> = emptyList(),
    onDismiss: () -> Unit,
    onShare: (String) -> Unit,
    onRemoveUser: (Long) -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("Editor") }
    var expandedRoleMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.share_list_title),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF303F4F)
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = stringResource(id = R.string.create_entity_close),
                            tint = Color(0xFF6DCB5A)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Email input con selector de rol
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                            placeholder = { Text(stringResource(id = R.string.login_email), color = Color.Gray) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            focusedBorderColor = Color(0xFF6DCB5A)
                        ),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Sección de integrantes
                if (currentUsers.isNotEmpty()) {
                    Text(
                        text = stringResource(id = R.string.share_list_members_title),
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(currentUsers) { user ->
                            SharedUserItem(
                                user = user,
                                onRemove = if (user.role != "Creador") {
                                    { onRemoveUser(user.id) }
                                } else null
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Botón enviar
                Button(
                    onClick = {
                        if (email.isNotBlank()) {
                            onShare(email)
                            email = ""
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6DCB5A)
                    ),
                    enabled = email.isNotBlank()
                ) {
                    Text(
                        text = stringResource(id = R.string.share_button_text),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun SharedUserItem(
    user: SharedUser,
    onRemove: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF6DCB5A)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = user.photoRes),
                    contentDescription = user.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Nombre
            Text(
                text = user.name,
                fontSize = 16.sp,
                color = Color(0xFF303F4F),
                fontWeight = FontWeight.Medium
            )
        }

        if (onRemove != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = user.role,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = stringResource(id = R.string.options_delete),
                        tint = Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        } else {
            Text(
                text = user.role,
                fontSize = 14.sp,
                color = Color(0xFF6DCB5A),
                fontWeight = FontWeight.Medium
            )
        }
    }
}