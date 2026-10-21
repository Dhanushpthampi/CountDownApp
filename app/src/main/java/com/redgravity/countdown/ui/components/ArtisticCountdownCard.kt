package com.redgravity.countdown.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
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
fun MaterialCountdownCard(
    item: CountdownItem,
    currentTimeMillis: Long,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val remaining = item.calculateRemaining(currentTimeMillis)

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        )
    ) {
        when (item.cardStyle) {
            CountdownCardStyle.STANDARD -> StandardLayout(item, remaining)
            CountdownCardStyle.COMPACT -> CompactLayout(item, remaining)
            CountdownCardStyle.PROGRESS_BAR -> ProgressBarLayout(item, remaining)
            CountdownCardStyle.PROGRESS_CIRCLE -> ProgressCircleLayout(item, remaining)
        }
    }
}

@Composable
private fun StandardLayout(item: CountdownItem, remaining: CountdownTimeRemaining) {
    Column(modifier = Modifier.padding(16.dp)) {
        // Category chip
        SuggestionChip(
            onClick = {},
            label = {
                Text(
                    text = item.category.title,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = item.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        if (item.description.isNotBlank()) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(Modifier.height(16.dp))

        if (remaining.isFinished) {
            ArrivedBadge()
        } else {
            // Primary readable countdown summary
            Text(
                text = remaining.readableRemaining,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            // Sub-row: hrs / min / sec when under a month
            if (remaining.calYears < 1 && remaining.calMonths < 1) {
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TimeBox(value = remaining.formattedHours, label = "hrs", modifier = Modifier.weight(1f))
                    TimeBox(value = remaining.formattedMinutes, label = "min", modifier = Modifier.weight(1f))
                    TimeBox(value = remaining.formattedSeconds, label = "sec", modifier = Modifier.weight(1f))
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = TimeUtils.formatEditorialDate(item.targetEpochMillis),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CompactLayout(item: CountdownItem, remaining: CountdownTimeRemaining) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.category.title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = TimeUtils.formatEditorialDate(item.targetEpochMillis),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(Modifier.width(16.dp))

        Column(horizontalAlignment = Alignment.End) {
            if (remaining.isFinished) {
                Text(
                    text = "Done",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Text(
                    text = remaining.shortReadable,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "remaining",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ProgressBarLayout(item: CountdownItem, remaining: CountdownTimeRemaining) {
    Column(modifier = Modifier.padding(16.dp)) {
        SuggestionChip(onClick = {}, label = {
            Text(text = item.category.title, style = MaterialTheme.typography.labelSmall)
        })
        Spacer(Modifier.height(8.dp))
        Text(
            text = item.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(12.dp))

        if (remaining.isFinished) {
            ArrivedBadge()
        } else {
            Text(
                text = remaining.readableRemaining,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            if (remaining.hasProgress) {
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Started",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${(remaining.progressRatio * 100).toInt()}% elapsed",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { remaining.progressRatio },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(3.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primaryContainer,
                    strokeCap = StrokeCap.Round
                )
            }
        }

        Spacer(Modifier.height(8.dp))
        Text(
            text = TimeUtils.formatEditorialDate(item.targetEpochMillis),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ProgressCircleLayout(item: CountdownItem, remaining: CountdownTimeRemaining) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Progress Ring
        if (remaining.hasProgress) {
            MaterialProgressRing(
                progress = remaining.progressRatio,
                size = 80.dp
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${(remaining.progressRatio * 100).toInt()}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "%",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 9.sp
                    )
                }
            }
        } else {
            // No progress: show big number
            Box(
                modifier = Modifier.size(80.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (remaining.isFinished) "✓" else remaining.smartValue.toString(),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (!remaining.isFinished) {
                        Text(
                            text = remaining.smartUnit,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 9.sp
                        )
                    }
                }
            }
        }

        Spacer(Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.category.title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(4.dp))
            if (remaining.isFinished) {
                Text(
                    text = "Milestone reached!",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            } else {
                Text(
                    text = remaining.readableRemaining,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(2.dp))
            Text(
                text = TimeUtils.formatEditorialDate(item.targetEpochMillis),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TimeBox(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = false
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (isPrimary)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = if (isPrimary)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = if (isPrimary)
                    MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                else
                    MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 9.sp
            )
        }
    }
}

@Composable
fun MaterialProgressRing(
    progress: Float,
    size: androidx.compose.ui.unit.Dp,
    strokeWidth: androidx.compose.ui.unit.Dp = 6.dp,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {}
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(800),
        label = "progress_ring"
    )
    val primaryColor = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.primaryContainer

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size)
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val strokePx = strokeWidth.toPx()
            val diameter = size.toPx() - strokePx
            val radius = diameter / 2f
            val center = androidx.compose.ui.geometry.Offset(size.toPx() / 2f, size.toPx() / 2f)
            drawCircle(
                color = trackColor,
                radius = radius,
                center = center,
                style = Stroke(width = strokePx)
            )
            if (animatedProgress > 0f) {
                drawArc(
                    color = primaryColor,
                    startAngle = -90f,
                    sweepAngle = animatedProgress * 360f,
                    useCenter = false,
                    topLeft = androidx.compose.ui.geometry.Offset(strokePx / 2f, strokePx / 2f),
                    size = androidx.compose.ui.geometry.Size(diameter, diameter),
                    style = Stroke(width = strokePx, cap = StrokeCap.Round)
                )
            }
        }
        content()
    }
}

@Composable
private fun ArrivedBadge() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "🎉  Milestone reached!",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}
