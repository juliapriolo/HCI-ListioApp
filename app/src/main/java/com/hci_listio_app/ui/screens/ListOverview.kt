package com.hci_listio_app.ui.screens

import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hci_listio_app.R
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
    username: String = "Pedro",
    lists: List<ListOverviewItem> = emptyList(),
    modifier: Modifier = Modifier,
    onFabClick: () -> Unit = {},
    onItemClick: (String) -> Unit = {}
) {
    Column(modifier = modifier.fillMaxSize()) {
        ListHeader(username = username)

        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            val query = remember { mutableStateOf("") }
            SearchBar(query = query.value, onQueryChange = { query.value = it }, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(12.dp))

            val tabs = listOf("Personal", "Compartidas", "Historial")
            val selected = remember { mutableStateOf(0) }
            ListsTabs(tabs = tabs, selectedIndex = selected.value, onSelectedChange = { selected.value = it })

            Spacer(modifier = Modifier.height(16.dp))

            if (lists.isEmpty()) {
                EmptyState()
            } else {
                Column {
                    lists.forEach { item ->
                        OverviewCard(
                            item = OverviewItem(item.id, item.title, item.isPrivate, item.completed, item.total, item.members),
                            onClick = { onItemClick(item.id) }
                        )
                    }
                }
            }
        }

        FloatingActionButton(onClick = onFabClick, modifier = Modifier.padding(20.dp)) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar")
        }
    }
}

// --- Previews with sample data ---
@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun ListOverviewEmptyPreview() {
    ListOverview(username = "Pedro", lists = emptyList())
}

@Preview(showBackground = true)
@Composable
fun ListOverviewPopulatedPreview() {
    val items = listOf(
        ListOverviewItem(id = "1", title = "Compras Semanales", isPrivate = true, completed = 3, total = 9, members = listOf("Ana", "Miguel", "Luis", "Carla")),
        ListOverviewItem(id = "2", title = "Compras Cumpleaños", isPrivate = false, completed = 0, total = 20, members = listOf("Sofia", "Pablo")),
        ListOverviewItem(id = "3", title = "Lista viaje fin de semana", isPrivate = true, completed = 0, total = 16, members = listOf("Marta"))
    )

    ListOverview(username = "Pedro", lists = items)
}
