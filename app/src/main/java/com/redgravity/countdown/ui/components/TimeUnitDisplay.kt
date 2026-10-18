package com.redgravity.countdown.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.redgravity.countdown.model.CountdownTimeRemaining

@Composable
fun MetricBox(
    value: String,
    label: String,
    accentColor: Color,
    backgroundColor: Color = Color(0x15FFFFFF),
    borderColor: Color = Color(0x20FFFFFF),
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .padding(vertical = 10.dp, horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = accentColor,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.sp,
                letterSpacing = 1.sp,
                color = Color(0xFF9FA6B6),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun FullTimeUnitGrid(
    remaining: CountdownTimeRemaining,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        MetricBox(
            value = remaining.formattedDays,
            label = remaining.daysLabel,
            accentColor = accentColor,
            modifier = Modifier.weight(1f)
        )
        MetricBox(
            value = remaining.formattedHours,
            label = "Hours",
            accentColor = accentColor,
            modifier = Modifier.weight(1f)
        )
        MetricBox(
            value = remaining.formattedMinutes,
            label = "Mins",
            accentColor = accentColor,
            modifier = Modifier.weight(1f)
        )
        MetricBox(
            value = remaining.formattedSeconds,
            label = "Secs",
            accentColor = accentColor,
            modifier = Modifier.weight(1f)
        )
    }
}
