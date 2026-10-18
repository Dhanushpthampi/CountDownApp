package com.redgravity.countdown.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.redgravity.countdown.model.CountdownCardStyle
import com.redgravity.countdown.model.CountdownItem
import com.redgravity.countdown.model.CountdownTimeRemaining
import com.redgravity.countdown.util.TimeUtils

@Composable
fun ArtisticCountdownCard(
    item: CountdownItem,
    currentTimeMillis: Long,
    onClick: () -> Unit,
    onTogglePin: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = item.visualTheme
    val remaining = item.calculateRemaining(currentTimeMillis)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(theme.surfaceGradientStart, theme.surfaceGradientEnd)
                )
            )
            .border(
                width = 1.dp,
                color = if (item.isPinned) theme.accentColor.copy(alpha = 0.6f) else theme.cardBorder,
                shape = RoundedCornerShape(22.dp)
            )
            .clickable(onClick = onClick)
            .padding(20.dp)
    ) {
        when (item.cardStyle) {
            CountdownCardStyle.ARCHITECTURAL -> {
                ArchitecturalLayout(
                    item = item,
                    remaining = remaining,
                    onTogglePin = onTogglePin,
                    onShare = onShare
                )
            }
            CountdownCardStyle.NEO_GLASS -> {
                NeoGlassLayout(
                    item = item,
                    remaining = remaining,
                    onTogglePin = onTogglePin,
                    onShare = onShare
                )
            }
            CountdownCardStyle.RADIAL_PROGRESS -> {
                RadialProgressLayout(
                    item = item,
                    remaining = remaining,
                    onTogglePin = onTogglePin,
                    onShare = onShare
                )
            }
            CountdownCardStyle.BOLD_NUMERIC -> {
                BoldNumericLayout(
                    item = item,
                    remaining = remaining,
                    onTogglePin = onTogglePin,
                    onShare = onShare
                )
            }
        }
    }
}

@Composable
private fun ArchitecturalLayout(
    item: CountdownItem,
    remaining: CountdownTimeRemaining,
    onTogglePin: () -> Unit,
    onShare: () -> Unit
) {
    val theme = item.visualTheme

    Column {
        // Top Meta Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "✦  ${item.category.title.uppercase()}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    color = theme.accentColor
                )
                Spacer(Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(3.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF67728A))
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = TimeUtils.formatRelativeStatus(item.targetEpochMillis),
                    fontSize = 11.sp,
                    color = Color(0xFF9FA6B6)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onTogglePin,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (item.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                        contentDescription = "Pin",
                        tint = if (item.isPinned) theme.accentColor else Color(0xFF67728A),
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(Modifier.width(4.dp))

                IconButton(
                    onClick = onShare,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = "Share",
                        tint = Color(0xFF8E95A5),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        // Title & Description
        Text(
            text = item.title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = theme.textPrimary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        if (item.description.isNotBlank()) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodySmall,
                color = theme.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(Modifier.height(14.dp))

        // Hairline Divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(theme.cardBorder)
        )

        Spacer(Modifier.height(16.dp))

        if (remaining.isFinished) {
            ArrivedBanner(theme.accentColor)
        } else {
            FullTimeUnitGrid(
                remaining = remaining,
                accentColor = theme.accentColor
            )
        }

        Spacer(Modifier.height(12.dp))

        // Footer with Target Date
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Target: ${TimeUtils.formatEditorialDate(item.targetEpochMillis)}",
                fontSize = 11.sp,
                color = Color(0xFF7E889B)
            )
            Text(
                text = TimeUtils.formatTime(item.targetEpochMillis),
                fontSize = 11.sp,
                color = Color(0xFF7E889B),
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
private fun NeoGlassLayout(
    item: CountdownItem,
    remaining: CountdownTimeRemaining,
    onTogglePin: () -> Unit,
    onShare: () -> Unit
) {
    val theme = item.visualTheme

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(theme.accentColor.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = item.category.title.uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = theme.accentColor
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onTogglePin, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = if (item.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                        contentDescription = "Pin",
                        tint = if (item.isPinned) theme.accentColor else Color(0xFF67728A),
                        modifier = Modifier.size(16.dp)
                    )
                }
                IconButton(onClick = onShare, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = "Share",
                        tint = Color(0xFF8E95A5),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = item.title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )

        Spacer(Modifier.height(14.dp))

        if (remaining.isFinished) {
            ArrivedBanner(theme.accentColor)
        } else {
            FullTimeUnitGrid(
                remaining = remaining,
                accentColor = theme.accentColor,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(14.dp))

        // Linear Progress bar showing journey completion
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${(remaining.progressRatio * 100).toInt()}% Elapsed",
                    fontSize = 10.sp,
                    color = Color(0xFF9FA6B6),
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = TimeUtils.formatShortDate(item.targetEpochMillis),
                    fontSize = 10.sp,
                    color = Color(0xFF9FA6B6)
                )
            }
            Spacer(Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { remaining.progressRatio },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = theme.accentColor,
                trackColor = Color(0x25FFFFFF),
                strokeCap = StrokeCap.Round
            )
        }
    }
}

@Composable
private fun RadialProgressLayout(
    item: CountdownItem,
    remaining: CountdownTimeRemaining,
    onTogglePin: () -> Unit,
    onShare: () -> Unit
) {
    val theme = item.visualTheme

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left Column: Radial Ring
        RadialProgressRing(
            progress = remaining.progressRatio,
            accentColor = theme.accentColor,
            secondaryColor = theme.secondaryAccent,
            size = 84.dp,
            strokeWidth = 6.dp
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (remaining.isFinished) "0" else remaining.days.toString(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = theme.accentColor
                )
                Text(
                    text = "DAYS",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                    color = Color(0xFF9FA6B6)
                )
            }
        }

        Spacer(Modifier.width(16.dp))

        // Right Column: Details
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "✦  ${item.category.title.uppercase()}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = theme.accentColor
                )

                Row {
                    IconButton(onClick = onTogglePin, modifier = Modifier.size(24.dp)) {
                        Icon(
                            imageVector = if (item.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                            contentDescription = "Pin",
                            tint = if (item.isPinned) theme.accentColor else Color(0xFF67728A),
                            modifier = Modifier.size(15.dp)
                        )
                    }
                    IconButton(onClick = onShare, modifier = Modifier.size(24.dp)) {
                        Icon(
                            imageVector = Icons.Outlined.Share,
                            contentDescription = "Share",
                            tint = Color(0xFF8E95A5),
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(4.dp))

            Text(
                text = item.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(6.dp))

            if (remaining.isFinished) {
                Text(
                    text = "Arrived • Target Reached",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = theme.accentColor
                )
            } else {
                Text(
                    text = "${remaining.formattedHours}h ${remaining.formattedMinutes}m ${remaining.formattedSeconds}s remaining",
                    fontSize = 12.sp,
                    color = Color(0xFFC7CDD9),
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Target: ${TimeUtils.formatShortDate(item.targetEpochMillis)}",
                fontSize = 11.sp,
                color = Color(0xFF7E889B)
            )
        }
    }
}

@Composable
private fun BoldNumericLayout(
    item: CountdownItem,
    remaining: CountdownTimeRemaining,
    onTogglePin: () -> Unit,
    onShare: () -> Unit
) {
    val theme = item.visualTheme

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "✦  ${item.category.title.uppercase()}  •  ${TimeUtils.formatRelativeStatus(item.targetEpochMillis)}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = theme.accentColor
            )

            Row {
                IconButton(onClick = onTogglePin, modifier = Modifier.size(26.dp)) {
                    Icon(
                        imageVector = if (item.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                        contentDescription = "Pin",
                        tint = if (item.isPinned) theme.accentColor else Color(0xFF67728A),
                        modifier = Modifier.size(16.dp)
                    )
                }
                IconButton(onClick = onShare, modifier = Modifier.size(26.dp)) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = "Share",
                        tint = Color(0xFF8E95A5),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = item.title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(Modifier.height(8.dp))

        if (remaining.isFinished) {
            ArrivedBanner(theme.accentColor)
        } else {
            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = remaining.days.toString(),
                    fontSize = 52.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    color = theme.accentColor
                )
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.padding(bottom = 8.dp)) {
                    Text(
                        text = "${remaining.daysLabel} LEFT",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = Color.White
                    )
                    Text(
                        text = "${remaining.formattedHours}H : ${remaining.formattedMinutes}M : ${remaining.formattedSeconds}S",
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF8E95A5)
                    )
                }
            }
        }

        Spacer(Modifier.height(6.dp))

        Text(
            text = "Until ${TimeUtils.formatEditorialDate(item.targetEpochMillis)}",
            fontSize = 11.sp,
            color = Color(0xFF7E889B)
        )
    }
}

@Composable
private fun ArrivedBanner(accentColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(accentColor.copy(alpha = 0.15f))
            .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(vertical = 12.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "MOMENT ARRIVED",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                color = accentColor
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "This milestone has reached its horizon.",
                fontSize = 12.sp,
                color = Color(0xFFC7CDD9)
            )
        }
    }
}
