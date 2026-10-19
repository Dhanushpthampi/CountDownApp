package com.redgravity.countdown.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.redgravity.countdown.model.CountdownCategory

@Composable
fun CategoryChipRow(
    selectedCategory: CountdownCategory,
    onCategorySelected: (CountdownCategory) -> Unit,
    accentColor: Color = Color.Unspecified, // kept for backward compat, unused with M3
    includeAll: Boolean = true,
    modifier: Modifier = Modifier
) {
    val categories = if (includeAll) {
        CountdownCategory.entries
    } else {
        CountdownCategory.entries.filter { it != CountdownCategory.ALL }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.forEach { category ->
            FilterChip(
                selected = category == selectedCategory,
                onClick = { onCategorySelected(category) },
                label = { Text(category.title) }
            )
        }
    }
}
