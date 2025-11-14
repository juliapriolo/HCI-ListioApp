package com.hci_listio_app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.hci_listio_app.R
import com.hci_listio_app.ui.Components.BottomNavigationBar
import com.hci_listio_app.ui.Components.EmptyState
import com.hci_listio_app.ui.Components.ListHeader
import com.hci_listio_app.ui.Components.ListsTabs
import com.hci_listio_app.ui.Components.OverviewCard
import com.hci_listio_app.ui.Components.OverviewItem
import com.hci_listio_app.ui.Components.SearchBar

data class ListOverviewItem(
    val id: String,
    val title: String,
    val isPrivate: Boolean = true,
    val completed: Int = 0,
    val total: Int = 0,
    val members: List<String> = emptyList()
)

@Composable
fun ListOverview(
    navController: NavController,
    username: String = "Pedro",
    lists: List<ListOverviewItem> = emptyList(),
    modifier: Modifier = Modifier,
    onFabClick: () -> Unit = {},
    onItemClick: (String) -> Unit = {}
) {
    val query = remember { mutableStateOf("") }
    val tabs = listOf("Personal", "Compartidas", "Historial")
    val selected = remember { mutableStateOf(0) }

    androidx.compose.material3.Scaffold(
        containerColor = Color(0xFFFAFAFA),
        topBar = { ListHeader(username = username) },
        floatingActionButton = {
            androidx.compose.material3.FloatingActionButton(
                onClick = onFabClick,
                containerColor = Color.White,
                contentColor = Color(0xFF6DCB5A)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar")
            }
        },
        bottomBar = { BottomNavigationBar(navController = navController) },
        modifier = modifier
    ) { padding ->
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(padding)
            .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            SearchBar(query = query.value, onQueryChange = { query.value = it }, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(12.dp))

            ListsTabs(tabs = tabs, selectedIndex = selected.value, onSelectedChange = { selected.value = it }, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(12.dp))

            if (lists.isEmpty()) {
                EmptyState(modifier = Modifier.fillMaxWidth())
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    lists.forEach { item ->
                        OverviewCard(
                            item = OverviewItem(item.id, item.title, item.isPrivate, item.completed, item.total, item.members),
                            onClick = { onItemClick(item.id) },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

// --- Previews with sample data ---
@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun ListOverviewEmptyPreview() {
    ListOverview(navController = rememberNavController(), username = "Pedro", lists = emptyList())
}

@Preview(showBackground = true)
@Composable
fun ListOverviewPopulatedPreview() {
    val items = listOf(
        ListOverviewItem(id = "1", title = "Compras Semanales", isPrivate = true, completed = 3, total = 9, members = listOf("Ana", "Miguel", "Luis", "Carla")),
        ListOverviewItem(id = "2", title = "Compras Cumpleaños", isPrivate = false, completed = 0, total = 20, members = listOf("Sofia", "Pablo")),
        ListOverviewItem(id = "3", title = "Lista viaje fin de semana", isPrivate = true, completed = 0, total = 16, members = listOf("Marta"))
    )

    ListOverview(navController = rememberNavController(), username = "Pedro", lists = items)
}
