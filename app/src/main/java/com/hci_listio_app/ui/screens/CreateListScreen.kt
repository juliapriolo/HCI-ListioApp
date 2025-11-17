package com.hci_listio_app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.res.stringResource
import com.hci_listio_app.R
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.hci_listio_app.ui.Components.ListioTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateListScreen(
    navController: NavController
) {
    var listName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            ListioTopAppBar(
                title = stringResource(id = R.string.create_list),
                showBackButton = true,
                onBackClick = { navController.navigateUp() }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = stringResource(id = R.string.create_list_name), style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = listName,
                onValueChange = { listName = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(id = R.string.create_list_placeholder)) }
            )

            Button(onClick = {
                navController.navigateUp()
            }) {
                Text(text = stringResource(id = R.string.common_save))
            }

            TextButton(onClick = { navController.navigateUp() }) {
                Text(text = stringResource(id = R.string.common_cancel))
            }
        }
    }
}
