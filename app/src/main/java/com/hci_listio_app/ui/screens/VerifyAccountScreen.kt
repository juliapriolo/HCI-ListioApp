package com.hci_listio_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.hci_listio_app.ui.Components.ListioTopAppBar
import com.hci_listio_app.ui.navigation.Screen
import com.hci_listio_app.ui.viewmodels.VerifyAccountViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyAccountScreen(
    navController: NavController,
    email: String = "",
    password: String = "",
    viewModel: VerifyAccountViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Inicializar el ViewModel con email si está disponible
    LaunchedEffect(email) {
        if (email.isNotEmpty()) {
            viewModel.initialize(email)
        }
    }

    // Navegar a Login cuando la verificación sea exitosa
    LaunchedEffect(uiState.isVerificationSuccessful) {
        if (uiState.isVerificationSuccessful) {
            navController.navigate(Screen.Login.route) {
                popUpTo(Screen.SignUp.route) { inclusive = true }
            }
            viewModel.consumeVerificationSuccess()
        }
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            ListioTopAppBar(
                title = "Verificar cuenta",
                showBackButton = true,
                onBackClick = { navController.navigateUp() }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Main Content - White Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                        .padding(32.dp)
                ) {
                    // Title
                    Text(
                        text = "Verificar cuenta",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF303F4F),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Description
                    Text(
                        text = "Ingresá el código de verificación que enviamos a ${uiState.email.ifEmpty { "tu email" }}",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    // Code Field
                    OutlinedTextField(
                        value = uiState.code,
                        onValueChange = viewModel::onCodeChange,
                        placeholder = { Text("Código de verificación") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Código Icon",
                                tint = Color.Gray
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF6DCB5A),
                            unfocusedBorderColor = Color.LightGray
                        )
                    )

                    // Verificar Button
                    Button(
                        onClick = viewModel::onSubmit,
                        enabled = !uiState.isLoading && !uiState.isResendingCode,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                            .height(50.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6DCB5A))
                    ) {
                        Text("Verificar cuenta", color = Color.White, fontSize = 16.sp)
                    }

                    // Reenviar código Button
                    TextButton(
                        onClick = viewModel::onResendCode,
                        enabled = !uiState.isLoading && !uiState.isResendingCode && uiState.canResendCode,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        if (uiState.isResendingCode) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color(0xFF6DCB5A),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(
                            "Reenviar código",
                            color = if (uiState.canResendCode) Color(0xFF6DCB5A) else Color.Gray,
                            fontSize = 14.sp
                        )
                    }

                    // Error Message
                    uiState.errorMessage?.let { error ->
                        Text(
                            text = error,
                            color = Color.Red,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        )
                    }

                    // Success Message (si se reenvió el código)
                    if (!uiState.canResendCode && !uiState.isResendingCode && uiState.errorMessage == null) {
                        Text(
                            text = "Código reenviado. Revisá tu email.",
                            color = Color(0xFF6DCB5A),
                            fontSize = 14.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        )
                    }
                }
            }

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VerifyAccountScreenPreview() {
    VerifyAccountScreen(
        navController = rememberNavController(),
        email = "usuario@email.com",
        password = "password123"
    )
}

