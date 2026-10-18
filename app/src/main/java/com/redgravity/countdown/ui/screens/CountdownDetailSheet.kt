package com.redgravity.countdown.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.redgravity.countdown.data.WidgetBindingManager
import com.redgravity.countdown.model.CountdownItem
import com.redgravity.countdown.ui.components.FullTimeUnitGrid
import com.redgravity.countdown.ui.components.MetricBox
import com.redgravity.countdown.ui.components.RadialProgressRing
import com.redgravity.countdown.util.TimeUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountdownDetailSheet(
    item: CountdownItem,
    currentTimeMillis: Long,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onTogglePin: () -> Unit
) {
    val context = LocalContext.current
    val theme = item.visualTheme
    val remaining = item.calculateRemaining(currentTimeMillis)
    var showDeleteConfirm by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color(0xFF0F1117),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(44.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF282E42))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp)
        ) {
            // Header: Category & Close
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "✦  ${item.category.title.uppercase()}",
                        style = MaterialTheme.typography.labelMedium,
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = theme.accentColor
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "•  ${TimeUtils.formatRelativeStatus(item.targetEpochMillis, currentTimeMillis)}",
                        fontSize = 11.sp,
                        color = Color(0xFF8E95A5)
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Close",
                        tint = Color(0xFF9FA6B6)
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            // Title
            Text(
                text = item.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            if (item.description.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFC7CDD9),
                    lineHeight = 22.sp
                )
            }

            Spacer(Modifier.height(24.dp))

            // Hero Radial Chronos Arc Centerpiece
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(theme.surfaceGradientStart, theme.surfaceGradientEnd)
                        )
                    )
                    .border(1.dp, theme.cardBorder, RoundedCornerShape(24.dp))
                    .padding(vertical = 28.dp, horizontal = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    RadialProgressRing(
                        progress = remaining.progressRatio,
                        accentColor = theme.accentColor,
                        secondaryColor = theme.secondaryAccent,
                        size = 140.dp,
                        strokeWidth = 9.dp
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (remaining.isFinished) "0" else remaining.days.toString(),
                                fontSize = 42.sp,
                                fontWeight = FontWeight.Bold,
                                color = theme.accentColor,
                                fontFamily = FontFamily.SansSerif
                            )
                            Text(
                                text = "${remaining.daysLabel} LEFT",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = Color(0xFF9FA6B6)
                            )
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    if (remaining.isFinished) {
                        Text(
                            text = "MILESTONE REACHED",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = theme.accentColor,
                            letterSpacing = 2.sp
                        )
                    } else {
                        // Live High-Precision Clock
                        Text(
                            text = "${remaining.formattedHours} : ${remaining.formattedMinutes} : ${remaining.formattedSeconds}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = Color.White
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "HOURS   •   MINUTES   •   SECONDS",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.2.sp,
                            color = Color(0xFF8E95A5)
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = "${(remaining.progressRatio * 100).toInt()}% of the journey elapsed",
                        fontSize = 11.sp,
                        color = Color(0xFF7E889B)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // Breakdown Metrics
            Text(
                text = "TOTAL BREAKDOWN",
                style = MaterialTheme.typography.labelMedium,
                letterSpacing = 1.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF9FA6B6)
            )

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricBox(
                    value = remaining.totalWeeks.toString(),
                    label = "Weeks",
                    accentColor = theme.accentColor,
                    modifier = Modifier.weight(1f)
                )
                MetricBox(
                    value = (remaining.totalSeconds / 3600).toString(),
                    label = "Hours",
                    accentColor = theme.accentColor,
                    modifier = Modifier.weight(1f)
                )
                MetricBox(
                    value = (remaining.totalSeconds / 60).toString(),
                    label = "Minutes",
                    accentColor = theme.accentColor,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(20.dp))

            // Destination details
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF161922))
                    .border(1.dp, Color(0xFF282E42), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Target Date",
                            fontSize = 12.sp,
                            color = Color(0xFF8E95A5)
                        )
                        Text(
                            text = TimeUtils.formatEditorialDate(item.targetEpochMillis),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Target Time",
                            fontSize = 12.sp,
                            color = Color(0xFF8E95A5)
                        )
                        Text(
                            text = TimeUtils.formatTime(item.targetEpochMillis),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily.Monospace,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Action Buttons
            // 1. Pin to Home Screen Widget
            Button(
                onClick = {
                    val pinned = WidgetBindingManager.requestPinWidget(context, item.id)
                    if (pinned) {
                        Toast.makeText(context, "Adding widget to home screen...", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(
                            context,
                            "To add widget: Long press home screen -> Widgets -> Chronos",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = theme.accentColor,
                    contentColor = Color(0xFF141310)
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Widgets,
                    contentDescription = "Widget",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Add to Home Screen Widget",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Spacer(Modifier.height(10.dp))

            // 2. Share & Pin row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        shareCountdown(context, item)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = "Share",
                        modifier = Modifier.size(16.dp),
                        tint = Color.White
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("Share", color = Color.White)
                }

                OutlinedButton(
                    onClick = onTogglePin,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PushPin,
                        contentDescription = "Pin",
                        modifier = Modifier.size(16.dp),
                        tint = if (item.isPinned) theme.accentColor else Color.White
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = if (item.isPinned) "Unpin" else "Pin Top",
                        color = Color.White
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            // 3. Edit & Delete row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "Edit",
                        modifier = Modifier.size(16.dp),
                        tint = Color.White
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("Edit", color = Color.White)
                }

                OutlinedButton(
                    onClick = { showDeleteConfirm = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFFF6B6B)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFFFF6B6B)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("Delete", color = Color(0xFFFF6B6B))
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            containerColor = Color(0xFF1B1E2B),
            title = {
                Text(
                    text = "Delete Countdown?",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete \"${item.title}\"? This action cannot be undone.",
                    color = Color(0xFFC7CDD9)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirm = false
                        onDelete()
                    }
                ) {
                    Text("Delete", color = Color(0xFFFF6B6B), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel", color = Color(0xFF9FA6B6))
                }
            }
        )
    }
}

private fun shareCountdown(context: Context, item: CountdownItem) {
    val remaining = item.calculateRemaining()
    val summary = if (remaining.isFinished) {
        "\"${item.title}\" has reached its moment on ${TimeUtils.formatEditorialDate(item.targetEpochMillis)}."
    } else {
        "\"${item.title}\": ${remaining.days} days, ${remaining.hours} hours remaining until ${TimeUtils.formatEditorialDate(item.targetEpochMillis)}."
    }

    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, item.title)
        putExtra(Intent.EXTRA_TEXT, "$summary\n\nTracked with Chronos")
    }
    context.startActivity(Intent.createChooser(sendIntent, "Share Countdown"))
}
