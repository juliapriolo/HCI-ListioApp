package com.hci_listio_app.ui.Components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun ListsTabs(
    tabs: List<String>,
    selectedIndex: Int,
    onSelectedChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.padding(4.dp)) {
            tabs.forEachIndexed { index, title ->
                val isSelected = index == selectedIndex
                val bg = if (isSelected) androidx.compose.ui.graphics.Color(0xFF2E7D32) else MaterialTheme.colorScheme.surface
                val txtColor = if (isSelected) androidx.compose.ui.graphics.Color.White else MaterialTheme.colorScheme.onSurface
                Surface(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onSelectedChange(index) }
                        .background(bg),
                    color = bg
                ) {
                    Text(text = title, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), color = txtColor)
                }
            }
        }
    }
}
