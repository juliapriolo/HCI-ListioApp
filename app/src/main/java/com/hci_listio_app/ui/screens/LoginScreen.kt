package com.hci_listio_app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.res.stringResource
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
import com.hci_listio_app.ui.viewmodels.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
            viewModel.consumeLoginSuccess()
        }
    }

    // Redirección automática a verificación cuando se detecta cuenta no verificada
    LaunchedEffect(uiState.shouldNavigateToVerification) {
        if (uiState.shouldNavigateToVerification && uiState.verificationEmail.isNotEmpty()) {
            navController.navigate(
                Screen.VerifyAccount.createRoute(uiState.verificationEmail)
            ) {
                popUpTo(Screen.Login.route) { inclusive = false }
            }
            viewModel.consumeNavigationToVerification()
        }
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            ListioTopAppBar(
                title = stringResource(R.string.login_title)
            )
        }
    ) { padding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val isTablet = maxWidth >= 600.dp
            val isLandscape = maxWidth > maxHeight
            
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = if (isTablet) Alignment.Center else Alignment.TopStart
            ) {
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
                Text(
                    text = stringResource(R.string.login_welcome),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF303F4F),
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                Image(
                    painter = painterResource(id = R.drawable.shopping_cart),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(120.dp)
                        .padding(top = 16.dp)
                        .align(Alignment.CenterHorizontally)
                )
                OutlinedTextField(
                    value = uiState.email,
                    onValueChange = {
                        if (uiState.errorMessage != null) {
                            viewModel.dismissError()
                        }
                        viewModel.onEmailChange(it)
                    },
                    label = { Text(stringResource(R.string.login_email)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = stringResource(R.string.login_email_icon)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp)
                )
                OutlinedTextField(
                    value = uiState.password,
                    onValueChange = {
                        if (uiState.errorMessage != null) {
                            viewModel.dismissError()
                        }
                        viewModel.onPasswordChange(it)
                    },
                    label = { Text(stringResource(R.string.login_password)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = stringResource(R.string.login_password_icon)
                        )
                    },
                    visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (uiState.isPasswordVisible) {
                            Icons.Filled.Visibility
                        } else {
                            Icons.Filled.VisibilityOff
                        }

                        val description = if (uiState.isPasswordVisible) 
                            stringResource(R.string.login_hide_password) 
                        else 
                            stringResource(R.string.login_show_password)

                        IconButton(onClick = viewModel::togglePasswordVisibility) {
                            Icon(imageVector = image, contentDescription = description)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.login_forgot_password),
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = stringResource(R.string.login_forgot_password_click),
                        fontSize = 14.sp,
                        color = Color(0xFF303F4F),
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable { navController.navigate(Screen.ForgotPassword.route) }
                    )
                }
                Button(
                    onClick = { viewModel.onSubmit() },
                    modifier = Modifier
                        .then(
                            if (isTablet || isLandscape) {
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
                    Text(stringResource(R.string.login_button), color = Color.White, fontSize = 16.sp)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.login_no_account),
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = stringResource(R.string.login_signup),
                        fontSize = 14.sp,
                        color = Color(0xFF303F4F),
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable { navController.navigate(Screen.SignUp.route) }
                    )
                }

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
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(
        navController = rememberNavController(),
        viewModel = LoginViewModel()
    )
}

