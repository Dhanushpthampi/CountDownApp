package com.redgravity.countdown.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.redgravity.countdown.model.CountdownCategory

@Composable
fun CategoryChipRow(
    selectedCategory: CountdownCategory,
    onCategorySelected: (CountdownCategory) -> Unit,
    accentColor: Color = Color(0xFFE5C07B),
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
            val isSelected = category == selectedCategory

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        if (isSelected) accentColor.copy(alpha = 0.18f)
                        else Color(0xFF141720)
                    )
                    .border(
                        width = 1.dp,
                        color = if (isSelected) accentColor else Color(0xFF262C3D),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clickable { onCategorySelected(category) }
                    .padding(horizontal = 14.dp, vertical = 7.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = category.title,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) accentColor else Color(0xFF8E95A5),
                    letterSpacing = 0.3.sp
                )
            }
        }
    }
}
