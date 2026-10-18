package com.redgravity.countdown.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.redgravity.countdown.model.CountdownCardStyle
import com.redgravity.countdown.model.CountdownCategory
import com.redgravity.countdown.model.CountdownItem
import com.redgravity.countdown.model.CountdownVisualTheme
import com.redgravity.countdown.ui.components.ArtisticCountdownCard
import com.redgravity.countdown.ui.components.CardStylePicker
import com.redgravity.countdown.ui.components.CategoryChipRow
import com.redgravity.countdown.ui.components.VisualThemePicker
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

    var title by remember {
        mutableStateOf(initialItem?.title ?: "")
    }
    var description by remember {
        mutableStateOf(initialItem?.description ?: "")
    }
    var category by remember {
        mutableStateOf(initialItem?.category ?: CountdownCategory.MILESTONE)
    }
    var themeId by remember {
        mutableStateOf(initialItem?.themeId ?: CountdownVisualTheme.OBSIDIAN_GOLD.id)
    }
    var cardStyle by remember {
        mutableStateOf(initialItem?.cardStyle ?: CountdownCardStyle.ARCHITECTURAL)
    }
    var targetMillis by remember {
        mutableLongStateOf(
            initialItem?.targetEpochMillis ?: run {
                TimeUtils.createPresetCalendar(daysOffset = 30).timeInMillis
            }
        )
    }
    var isPinned by remember {
        mutableStateOf(initialItem?.isPinned ?: false)
    }
    var titleError by remember { mutableStateOf(false) }

    // Live preview item
    val previewItem = remember(title, description, category, themeId, cardStyle, targetMillis, isPinned) {
        CountdownItem(
            id = initialItem?.id ?: "preview",
            title = title.ifBlank { "Milestone Title" },
            description = description,
            category = category,
            themeId = themeId,
            cardStyle = cardStyle,
            targetEpochMillis = targetMillis,
            createdEpochMillis = initialItem?.createdEpochMillis ?: System.currentTimeMillis(),
            isPinned = isPinned
        )
    }

    val activeTheme = CountdownVisualTheme.fromId(themeId)

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
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = if (initialItem == null) "NEW MILESTONE" else "EDIT MILESTONE",
                        style = MaterialTheme.typography.labelMedium,
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = activeTheme.accentColor
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = if (initialItem == null) "Craft a Countdown" else "Refine Countdown",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Close",
                        tint = Color(0xFF9FA6B6)
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            // Real-Time Live Preview Card
            Text(
                text = "LIVE DESIGN PREVIEW",
                style = MaterialTheme.typography.labelMedium,
                letterSpacing = 1.2.sp,
                color = Color(0xFF7E889B),
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            ArtisticCountdownCard(
                item = previewItem,
                currentTimeMillis = currentTimeMillis,
                onClick = {},
                onTogglePin = { isPinned = !isPinned },
                onShare = {}
            )

            Spacer(Modifier.height(24.dp))

            // Title Field
            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    if (titleError && it.isNotBlank()) titleError = false
                },
                label = { Text("Countdown Title") },
                placeholder = { Text("e.g. Total Solar Eclipse, Album Release, Tokyo Trip") },
                isError = titleError,
                supportingText = if (titleError) {
                    { Text("Title cannot be empty", color = Color(0xFFFF6B6B)) }
                } else null,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = activeTheme.accentColor,
                    unfocusedBorderColor = Color(0xFF282E42),
                    focusedLabelColor = activeTheme.accentColor,
                    unfocusedLabelColor = Color(0xFF8E95A5),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF141720),
                    unfocusedContainerColor = Color(0xFF141720)
                )
            )

            Spacer(Modifier.height(14.dp))

            // Description / Note Field
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Notes & Details (Optional)") },
                placeholder = { Text("Add context, venue, flight codes, or personal motto...") },
                minLines = 2,
                maxLines = 4,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = activeTheme.accentColor,
                    unfocusedBorderColor = Color(0xFF282E42),
                    focusedLabelColor = activeTheme.accentColor,
                    unfocusedLabelColor = Color(0xFF8E95A5),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF141720),
                    unfocusedContainerColor = Color(0xFF141720)
                )
            )

            Spacer(Modifier.height(20.dp))

            // Category Chips
            Text(
                text = "CATEGORY",
                style = MaterialTheme.typography.labelMedium,
                letterSpacing = 1.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF9FA6B6)
            )
            Spacer(Modifier.height(8.dp))
            CategoryChipRow(
                selectedCategory = category,
                onCategorySelected = { category = it },
                accentColor = activeTheme.accentColor,
                includeAll = false
            )

            Spacer(Modifier.height(24.dp))

            // Target Date & Time Selectors
            Text(
                text = "TARGET DATE & TIME",
                style = MaterialTheme.typography.labelMedium,
                letterSpacing = 1.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF9FA6B6)
            )

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Date Picker Button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF141720))
                        .border(1.dp, Color(0xFF282E42), RoundedCornerShape(14.dp))
                        .clickable {
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
                        }
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.CalendarToday,
                            contentDescription = "Date",
                            tint = activeTheme.accentColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Date",
                                fontSize = 10.sp,
                                color = Color(0xFF8E95A5),
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = TimeUtils.formatCompactDate(targetMillis),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }
                }

                // Time Picker Button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF141720))
                        .border(1.dp, Color(0xFF282E42), RoundedCornerShape(14.dp))
                        .clickable {
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
                        }
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.AccessTime,
                            contentDescription = "Time",
                            tint = activeTheme.accentColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Time",
                                fontSize = 10.sp,
                                color = Color(0xFF8E95A5),
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = TimeUtils.formatTime(targetMillis),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            // Quick Preset Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PresetChip("+1 Week") {
                    targetMillis = TimeUtils.createPresetCalendar(daysOffset = 7).timeInMillis
                }
                PresetChip("+1 Month") {
                    targetMillis = TimeUtils.createPresetCalendar(monthsOffset = 1).timeInMillis
                }
                PresetChip("+100 Days") {
                    targetMillis = TimeUtils.createPresetCalendar(daysOffset = 100).timeInMillis
                }
                PresetChip("+1 Year") {
                    targetMillis = TimeUtils.createPresetCalendar(yearsOffset = 1).timeInMillis
                }
                PresetChip("New Year") {
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
            }

            Spacer(Modifier.height(24.dp))

            // Visual Theme Palette Picker
            VisualThemePicker(
                selectedThemeId = themeId,
                onThemeSelected = { themeId = it.id }
            )

            Spacer(Modifier.height(24.dp))

            // Card Style Selector
            CardStylePicker(
                selectedStyle = cardStyle,
                accentColor = activeTheme.accentColor,
                onStyleSelected = { cardStyle = it }
            )

            Spacer(Modifier.height(20.dp))

            // Pin to Top Toggle
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF141720))
                    .border(1.dp, Color(0xFF282E42), RoundedCornerShape(14.dp))
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Pin to Showcase",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Text(
                            text = "Feature prominently at the top of your list",
                            fontSize = 11.sp,
                            color = Color(0xFF8E95A5)
                        )
                    }

                    Switch(
                        checked = isPinned,
                        onCheckedChange = { isPinned = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = activeTheme.accentColor,
                            checkedTrackColor = activeTheme.accentColor.copy(alpha = 0.3f),
                            uncheckedThumbColor = Color(0xFF8E95A5),
                            uncheckedTrackColor = Color(0xFF282E42)
                        )
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
                        themeId = themeId,
                        cardStyle = cardStyle,
                        targetEpochMillis = targetMillis,
                        createdEpochMillis = initialItem?.createdEpochMillis ?: System.currentTimeMillis(),
                        isPinned = isPinned
                    )
                    onSave(toSave)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = activeTheme.accentColor,
                    contentColor = Color(0xFF141310)
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = if (initialItem == null) "Create Countdown" else "Save Changes",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun PresetChip(
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF161922))
            .border(1.dp, Color(0xFF262C3D), RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color(0xFFC7CDD9),
            fontWeight = FontWeight.Medium
        )
    }
}
