package com.hci_listio_app.ui.Components

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListioTopAppBar(title: String) {
    CenterAlignedTopAppBar(
        title = { Text(title, color = Color.White) },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF6DCB5A))
    )
}