package com.hci_listio_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.hci_listio_app.R
import com.hci_listio_app.ui.Components.ListioTopAppBar
import com.hci_listio_app.ui.navigation.Screen
import com.hci_listio_app.ui.viewmodels.ForgotPasswordViewModel
import com.hci_listio_app.ui.viewmodels.ForgotPasswordStep

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    viewModel: ForgotPasswordViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Navegar a Login cuando el reset sea exitoso
    LaunchedEffect(uiState.isPasswordReset) {
        if (uiState.isPasswordReset) {
            navController.navigate(Screen.Login.route) {
                popUpTo(Screen.ForgotPassword.route) { inclusive = true }
            }
            viewModel.consumePasswordReset()
        }
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            ListioTopAppBar(
                title = stringResource(R.string.forgot_password_title),
                showBackButton = true,
                onBackClick = { navController.navigateUp() }
            )
        }
    ) { padding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val isTablet = maxWidth >= 600.dp
            
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = if (isTablet) Alignment.Center else Alignment.TopStart
            ) {
                // Main Content - White Card
                Column(
                    modifier = Modifier
                        .then(
                            if (isTablet) {
                                Modifier.widthIn(max = 600.dp)
                            } else {
                                Modifier.fillMaxWidth()
                            }
                        )
                        .verticalScroll(rememberScrollState())
                        .background(Color.White, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                        .padding(32.dp)
                ) {
                    when (uiState.step) {
                        ForgotPasswordStep.STEP_EMAIL -> {
                            // Paso 1: Ingresar email
                            Text(
                                text = stringResource(R.string.forgot_password_recover),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF303F4F),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            Text(
                                text = stringResource(R.string.forgot_password_description),
                                fontSize = 14.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(bottom = 24.dp)
                            )

                            OutlinedTextField(
                                value = uiState.email,
                                onValueChange = {
                                    if (uiState.errorMessage != null) {
                                        viewModel.dismissError()
                                    }
                                    viewModel.onEmailChange(it)
                                },
                                label = { Text(stringResource(R.string.forgot_password_email)) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Email,
                                        contentDescription = stringResource(R.string.forgot_password_email_icon)
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

                            Button(
                                onClick = viewModel::onSubmit,
                                enabled = !uiState.isLoading,
                                modifier = Modifier
                                    .then(
                                        if (isTablet) {
                                            Modifier.widthIn(min = 400.dp).align(Alignment.CenterHorizontally)
                                        } else {
                                            Modifier.fillMaxWidth()
                                        }
                                    )
                                    .padding(vertical = 16.dp)
                                    .height(50.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6DCB5A))
                            ) {
                                Text(stringResource(R.string.forgot_password_send_code), color = Color.White, fontSize = 16.sp)
                            }

                            // Success Message
                            if (uiState.isEmailSent) {
                                Text(
                                    text = stringResource(R.string.forgot_password_code_sent),
                                    color = Color(0xFF6DCB5A),
                                    fontSize = 14.sp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp)
                                )
                            }
                        }
                        ForgotPasswordStep.STEP_RESET -> {
                            // Paso 2: Ingresar código y nueva contraseña
                            Text(
                                text = stringResource(R.string.forgot_password_reset),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF303F4F),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            Text(
                                text = if (uiState.email.isNotEmpty()) {
                                    stringResource(R.string.forgot_password_reset_description, uiState.email)
                                } else {
                                    stringResource(R.string.forgot_password_reset_description_default)
                                },
                                fontSize = 14.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(bottom = 24.dp)
                            )

                            OutlinedTextField(
                                value = uiState.code,
                                onValueChange = {
                                    if (uiState.errorMessage != null) {
                                        viewModel.dismissError()
                                    }
                                    viewModel.onCodeChange(it)
                                },
                                label = { Text(stringResource(R.string.forgot_password_code)) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Email,
                                        contentDescription = stringResource(R.string.forgot_password_code_icon),
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

                            OutlinedTextField(
                                value = uiState.newPassword,
                                onValueChange = {
                                    if (uiState.errorMessage != null) {
                                        viewModel.dismissError()
                                    }
                                    viewModel.onNewPasswordChange(it)
                                },
                                label = { Text(stringResource(R.string.forgot_password_new_password)) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = stringResource(R.string.forgot_password_password_icon)
                                    )
                                },
                                visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                trailingIcon = {
                                    val image = if (uiState.isPasswordVisible) {
                                        Icons.Filled.Visibility
                                    } else {
                                        Icons.Filled.VisibilityOff
                                    }

                                    val description = if (uiState.isPasswordVisible) {
                                        stringResource(R.string.login_hide_password)
                                    } else {
                                        stringResource(R.string.login_show_password)
                                    }

                                    IconButton(onClick = viewModel::togglePasswordVisibility) {
                                        Icon(imageVector = image, contentDescription = description)
                                    }
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

                            OutlinedTextField(
                                value = uiState.confirmPassword,
                                onValueChange = {
                                    if (uiState.errorMessage != null) {
                                        viewModel.dismissError()
                                    }
                                    viewModel.onConfirmPasswordChange(it)
                                },
                                label = { Text(stringResource(R.string.forgot_password_confirm_password)) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = stringResource(R.string.forgot_password_confirm_password_icon)
                                    )
                                },
                                visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                trailingIcon = {
                                    val image = if (uiState.isPasswordVisible) {
                                        Icons.Filled.Visibility
                                    } else {
                                        Icons.Filled.VisibilityOff
                                    }

                                    val description = if (uiState.isPasswordVisible) {
                                        stringResource(R.string.login_hide_password)
                                    } else {
                                        stringResource(R.string.login_show_password)
                                    }

                                    IconButton(onClick = viewModel::togglePasswordVisibility) {
                                        Icon(imageVector = image, contentDescription = description)
                                    }
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

                            Button(
                                onClick = viewModel::onSubmit,
                                enabled = !uiState.isLoading,
                                modifier = Modifier
                                    .then(
                                        if (isTablet) {
                                            Modifier.widthIn(min = 400.dp).align(Alignment.CenterHorizontally)
                                        } else {
                                            Modifier.fillMaxWidth()
                                        }
                                    )
                                    .padding(vertical = 16.dp)
                                    .height(50.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6DCB5A))
                            ) {
                                Text(stringResource(R.string.forgot_password_reset_button), color = Color.White, fontSize = 16.sp)
                            }
                        }
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
fun ForgotPasswordScreenPreview() {
    ForgotPasswordScreen(
        navController = rememberNavController()
    )
}

