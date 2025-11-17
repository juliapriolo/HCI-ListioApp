package com.hci_listio_app.ui.screens

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.hci_listio_app.R
import com.hci_listio_app.ui.Components.ListioTopAppBar
import com.hci_listio_app.ui.viewmodels.LanguageViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val viewModel: LanguageViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return LanguageViewModel(context) as T
            }
        }
    )
    val uiState by viewModel.uiState.collectAsState()
    val activity = context as? Activity

    LaunchedEffect(uiState.isLanguageChanged) {
        if (uiState.isLanguageChanged) {
            viewModel.consumeLanguageChange()
        }
    }

    Scaffold(
        containerColor = Color(0xFFFAFAFA),
        topBar = {
            ListioTopAppBar(
                title = stringResource(R.string.language_title),
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
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                            .padding(32.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.language_select),
                            fontSize = 32.sp,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            color = Color(0xFF303F4F),
                            modifier = Modifier.padding(bottom = 24.dp)
                        )
                    }

                    
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column {
                                LanguageOption(
                                    label = stringResource(R.string.language_spanish),
                                    selected = uiState.selectedLanguage == "es",
                                    onClick = { viewModel.onLanguageSelected("es") }
                                )
                                HorizontalDivider(color = Color(0xFFE0E0E0))
                                LanguageOption(
                                    label = stringResource(R.string.language_english),
                                    selected = uiState.selectedLanguage == "en",
                                    onClick = { viewModel.onLanguageSelected("en") }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.size(8.dp))

                        Button(
                            onClick = { 
                                if (activity != null) {
                                    viewModel.saveLanguage(activity)
                                }
                            },
                            enabled = !uiState.isLoading,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6DCB5A)),
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
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = stringResource(R.string.language_save),
                                    color = Color.White,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
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


