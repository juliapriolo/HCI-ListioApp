package com.hci_listio_app.ui.Screens

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.hci_listio_app.ui.Components.ListioTopAppBar
import com.hci_listio_app.data.UserRepository
import com.hci_listio_app.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(navController: NavController) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val currentPhoto = UserRepository.photoBitmap.value
    val photoState = remember { mutableStateOf<Bitmap?>(currentPhoto) }

    val currentPassword = UserRepository.password.value
    val currentPasswordInput = remember { mutableStateOf("") }
    val newPasswordInput = remember { mutableStateOf("") }
    val confirmPasswordInput = remember { mutableStateOf("") }
    val passwordVisible = remember { mutableStateOf(false) }
    val newPasswordVisible = remember { mutableStateOf(false) }
    val confirmPasswordVisible = remember { mutableStateOf(false) }

    // Galería (solo opción activa)
    val context = LocalContext.current
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val bitmap = if (Build.VERSION.SDK_INT >= 28) {
                    val source = ImageDecoder.createSource(context.contentResolver, uri)
                    ImageDecoder.decodeBitmap(source)
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }
                photoState.value = bitmap
            } catch (_: Exception) { }
        }
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            ListioTopAppBar(
                title = "Editar perfil",
                showBackButton = true,
                onBackClick = { navController.navigateUp() }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Card principal similar a Login/SignUp
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .padding(24.dp)
            ) {
                Text(
                    text = "Editar Perfil",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF303F4F),
                )

                Spacer(Modifier.height(16.dp))

                // Avatar centrado con icono de editar superpuesto
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clip(CircleShape)
                            .background(Color(0xFF6DCB5A)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (photoState.value != null) {
                            Image(
                                bitmap = photoState.value!!.asImageBitmap(),
                                contentDescription = "Foto de perfil",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.perfilpredeterminado),
                                contentDescription = "Usuario",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                    IconButton(
                        onClick = { pickImageLauncher.launch("image/*") },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(28.dp)
                            .background(Color.White, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Editar foto",
                            tint = Color(0xFF6DCB5A)
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Campos de contraseña
                OutlinedTextField(
                    value = currentPasswordInput.value,
                    onValueChange = { currentPasswordInput.value = it },
                    label = { Text("Contraseña actual") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                            Icon(
                                imageVector = if (passwordVisible.value) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = null
                            )
                        }
                    }
                )

                OutlinedTextField(
                    value = newPasswordInput.value,
                    onValueChange = { newPasswordInput.value = it },
                    label = { Text("Nueva contraseña") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    visualTransformation = if (newPasswordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { newPasswordVisible.value = !newPasswordVisible.value }) {
                            Icon(
                                imageVector = if (newPasswordVisible.value) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = null
                            )
                        }
                    }
                )

                val passwordsMatch = newPasswordInput.value == confirmPasswordInput.value
                OutlinedTextField(
                    value = confirmPasswordInput.value,
                    onValueChange = { confirmPasswordInput.value = it },
                    label = { Text("Confirmar nueva contraseña") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    isError = confirmPasswordInput.value.isNotEmpty() && !passwordsMatch,
                    supportingText = {
                        if (confirmPasswordInput.value.isNotEmpty() && !passwordsMatch) {
                            Text("Las contraseñas no coinciden", color = Color.Red)
                        }
                    },
                    visualTransformation = if (confirmPasswordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible.value = !confirmPasswordVisible.value }) {
                            Icon(
                                imageVector = if (confirmPasswordVisible.value) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = null
                            )
                        }
                    }
                )

                // (Se reemplazó el botón de galería por el icono de editar en el avatar)

                Button(
                    onClick = {
                        // Validación simple: si ingresó actual (mock opcional) y coinciden nuevas
                        if (passwordsMatch) {
                            // Guardar bitmap si hay
                            UserRepository.updatePhoto(photoState.value)
                            // Guardar contraseña nueva (sin reglas, mock)
                            if (newPasswordInput.value.isNotEmpty()) {
                                UserRepository.updatePassword(newPasswordInput.value)
                            }
                            scope.launch {
                                snackbarHostState.showSnackbar("Cambios guardados")
                            }
                            navController.navigateUp()
                        }
                    },
                    enabled = passwordsMatch,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6DCB5A))
                ) {
                    Text("Guardar cambios", color = Color.White, fontSize = 16.sp)
                }
            }
        }
    }
}


