package com.redgravity.countdown.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.redgravity.countdown.model.CountdownCardStyle
import com.redgravity.countdown.model.CountdownCategory
import com.redgravity.countdown.model.CountdownItem
import com.redgravity.countdown.ui.components.CategoryChipRow
import com.redgravity.countdown.util.TimeUtils
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCountdownSheet(
    initialItem: CountdownItem?,
    currentTimeMillis: Long,
    onDismiss: () -> Unit,
    onSave: (CountdownItem) -> Unit
) {
    val context = LocalContext.current

    var title by remember { mutableStateOf(initialItem?.title ?: "") }
    var description by remember { mutableStateOf(initialItem?.description ?: "") }
    var category by remember { mutableStateOf(initialItem?.category ?: CountdownCategory.MILESTONE) }
    var cardStyle by remember { mutableStateOf(initialItem?.cardStyle ?: CountdownCardStyle.STANDARD) }
    var targetMillis by remember {
        mutableLongStateOf(
            initialItem?.targetEpochMillis ?: run {
                TimeUtils.createPresetCalendar(daysOffset = 30).timeInMillis
            }
        )
    }
    var titleError by remember { mutableStateOf(false) }

    // Optional start time
    var hasStartTime by remember { mutableStateOf(initialItem?.startEpochMillis != null) }
    var startMillis by remember {
        mutableLongStateOf(
            initialItem?.startEpochMillis ?: System.currentTimeMillis()
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp)
        ) {
            // Header
            Text(
                text = if (initialItem == null) "New countdown" else "Edit countdown",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(24.dp))

            // Title Field
            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    if (titleError && it.isNotBlank()) titleError = false
                },
                label = { Text("Title") },
                placeholder = { Text("e.g. Summer vacation, New Year, Graduation") },
                isError = titleError,
                supportingText = if (titleError) {
                    { Text("Title is required") }
                } else null,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.small
            )

            Spacer(Modifier.height(16.dp))

            // Description Field
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description (optional)") },
                minLines = 2,
                maxLines = 3,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.small
            )

            Spacer(Modifier.height(24.dp))

            // Category
            Text(
                text = "Category",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(8.dp))
            CategoryChipRow(
                selectedCategory = category,
                onCategorySelected = { category = it },
                includeAll = false
            )

            Spacer(Modifier.height(24.dp))

            HorizontalDivider()
            Spacer(Modifier.height(24.dp))

            // Target Date & Time
            Text(
                text = "Target date & time",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val c = Calendar.getInstance().apply { timeInMillis = targetMillis }
                        DatePickerDialog(
                            context,
                            { _, year, month, day ->
                                c.set(Calendar.YEAR, year)
                                c.set(Calendar.MONTH, month)
                                c.set(Calendar.DAY_OF_MONTH, day)
                                targetMillis = c.timeInMillis
                            },
                            c.get(Calendar.YEAR),
                            c.get(Calendar.MONTH),
                            c.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    },
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.small
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarToday,
                        contentDescription = "Date",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(TimeUtils.formatCompactDate(targetMillis))
                }

                OutlinedButton(
                    onClick = {
                        val c = Calendar.getInstance().apply { timeInMillis = targetMillis }
                        TimePickerDialog(
                            context,
                            { _, hour, minute ->
                                c.set(Calendar.HOUR_OF_DAY, hour)
                                c.set(Calendar.MINUTE, minute)
                                c.set(Calendar.SECOND, 0)
                                targetMillis = c.timeInMillis
                            },
                            c.get(Calendar.HOUR_OF_DAY),
                            c.get(Calendar.MINUTE),
                            false
                        ).show()
                    },
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.small
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccessTime,
                        contentDescription = "Time",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(TimeUtils.formatTime(targetMillis))
                }
            }

            // Quick Preset Chips
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "+1 Week" to { targetMillis = TimeUtils.createPresetCalendar(daysOffset = 7).timeInMillis },
                    "+1 Month" to { targetMillis = TimeUtils.createPresetCalendar(monthsOffset = 1).timeInMillis },
                    "+100 Days" to { targetMillis = TimeUtils.createPresetCalendar(daysOffset = 100).timeInMillis },
                    "+1 Year" to { targetMillis = TimeUtils.createPresetCalendar(yearsOffset = 1).timeInMillis },
                    "New Year" to {
                        val cal = Calendar.getInstance().apply {
                            set(Calendar.YEAR, get(Calendar.YEAR) + 1)
                            set(Calendar.MONTH, Calendar.JANUARY)
                            set(Calendar.DAY_OF_MONTH, 1)
                            set(Calendar.HOUR_OF_DAY, 0)
                            set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0)
                        }
                        targetMillis = cal.timeInMillis
                    }
                ).forEach { (label, action) ->
                    FilterChip(
                        selected = false,
                        onClick = action,
                        label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            HorizontalDivider()
            Spacer(Modifier.height(8.dp))

            // Optional Start Time for Progress
            ListItem(
                headlineContent = {
                    Text(
                        text = "Track progress",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                },
                supportingContent = {
                    Text(
                        text = "Show how much of the journey has elapsed",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Outlined.HourglassEmpty,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                trailingContent = {
                    Switch(
                        checked = hasStartTime,
                        onCheckedChange = { hasStartTime = it }
                    )
                }
            )

            if (hasStartTime) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Start date & time",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 0.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val c = Calendar.getInstance().apply { timeInMillis = startMillis }
                            DatePickerDialog(
                                context,
                                { _, year, month, day ->
                                    c.set(Calendar.YEAR, year)
                                    c.set(Calendar.MONTH, month)
                                    c.set(Calendar.DAY_OF_MONTH, day)
                                    startMillis = c.timeInMillis
                                },
                                c.get(Calendar.YEAR),
                                c.get(Calendar.MONTH),
                                c.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        },
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CalendarToday,
                            contentDescription = "Start Date",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(TimeUtils.formatCompactDate(startMillis))
                    }

                    OutlinedButton(
                        onClick = {
                            val c = Calendar.getInstance().apply { timeInMillis = startMillis }
                            TimePickerDialog(
                                context,
                                { _, hour, minute ->
                                    c.set(Calendar.HOUR_OF_DAY, hour)
                                    c.set(Calendar.MINUTE, minute)
                                    c.set(Calendar.SECOND, 0)
                                    startMillis = c.timeInMillis
                                },
                                c.get(Calendar.HOUR_OF_DAY),
                                c.get(Calendar.MINUTE),
                                false
                            ).show()
                        },
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AccessTime,
                            contentDescription = "Start Time",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(TimeUtils.formatTime(startMillis))
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Progress will be shown from the start date to target date.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(Modifier.height(16.dp))

            // Card Style
            Text(
                text = "Display style",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CountdownCardStyle.entries.forEach { style ->
                    FilterChip(
                        selected = cardStyle == style,
                        onClick = { cardStyle = style },
                        label = { Text(style.displayName, style = MaterialTheme.typography.labelMedium) }
                    )
                }
            }

            Spacer(Modifier.height(28.dp))

            // Save Button
            Button(
                onClick = {
                    if (title.isBlank()) {
                        titleError = true
                        return@Button
                    }
                    val toSave = CountdownItem(
                        id = initialItem?.id ?: java.util.UUID.randomUUID().toString(),
                        title = title.trim(),
                        description = description.trim(),
                        category = category,
                        cardStyle = cardStyle,
                        targetEpochMillis = targetMillis,
                        startEpochMillis = if (hasStartTime) startMillis else null,
                        createdEpochMillis = initialItem?.createdEpochMillis ?: System.currentTimeMillis()
                    )
                    onSave(toSave)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = if (initialItem == null) "Create countdown" else "Save changes",
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}
