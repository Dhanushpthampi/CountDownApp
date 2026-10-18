package com.redgravity.countdown.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.redgravity.countdown.model.CountdownCategory
import com.redgravity.countdown.model.CountdownItem
import com.redgravity.countdown.ui.components.ArtisticCountdownCard
import com.redgravity.countdown.ui.components.CategoryChipRow
import com.redgravity.countdown.util.TimeUtils
import kotlinx.coroutines.delay

@Composable
fun CountdownHomeScreen(
    countdowns: List<CountdownItem>,
    initialSelectedCountdownId: String? = null,
    onSaveCountdown: (CountdownItem) -> Unit,
    onDeleteCountdown: (String) -> Unit,
    onTogglePin: (String) -> Unit
) {
    val context = LocalContext.current

    // Live clock tick
    var currentTimeMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            currentTimeMillis = System.currentTimeMillis()
            delay(1000)
        }
    }

    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf(CountdownCategory.ALL) }
    var sortFurthestFirst by remember { mutableStateOf(false) }

    // Sheets state
    var editingItem by remember { mutableStateOf<CountdownItem?>(null) }
    var isCreateSheetOpen by remember { mutableStateOf(false) }
    var viewingDetailItem by remember {
        mutableStateOf<CountdownItem?>(
            if (initialSelectedCountdownId != null) {
                countdowns.firstOrNull { it.id == initialSelectedCountdownId }
            } else null
        )
    }

    // Keep viewingDetailItem in sync with changes to repository
    LaunchedEffect(countdowns, initialSelectedCountdownId) {
        if (initialSelectedCountdownId != null && viewingDetailItem == null) {
            viewingDetailItem = countdowns.firstOrNull { it.id == initialSelectedCountdownId }
        } else if (viewingDetailItem != null) {
            viewingDetailItem = countdowns.firstOrNull { it.id == viewingDetailItem!!.id }
        }
    }

    // Filter & Sort
    val filteredItems = remember(countdowns, searchQuery, selectedCategory, sortFurthestFirst) {
        countdowns
            .filter { item ->
                val matchesCategory = selectedCategory == CountdownCategory.ALL || item.category == selectedCategory
                val matchesSearch = searchQuery.isBlank() ||
                        item.title.contains(searchQuery, ignoreCase = true) ||
                        item.description.contains(searchQuery, ignoreCase = true)
                matchesCategory && matchesSearch
            }
            .sortedWith { a, b ->
                if (a.isPinned != b.isPinned) {
                    if (a.isPinned) -1 else 1
                } else if (sortFurthestFirst) {
                    b.targetEpochMillis.compareTo(a.targetEpochMillis)
                } else {
                    a.targetEpochMillis.compareTo(b.targetEpochMillis)
                }
            }
    }

    Scaffold(
        containerColor = Color(0xFF0C0E14),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingItem = null
                    isCreateSheetOpen = true
                },
                containerColor = Color(0xFFE5C07B),
                contentColor = Color(0xFF141310),
                shape = CircleShape,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create Countdown",
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Elegant Editorial App Header
            AppHeader(
                isSearchActive = isSearchActive,
                onToggleSearch = {
                    isSearchActive = !isSearchActive
                    if (!isSearchActive) searchQuery = ""
                },
                sortFurthestFirst = sortFurthestFirst,
                onToggleSort = { sortFurthestFirst = !sortFurthestFirst }
            )

            // Collapsible Search Input
            AnimatedVisibility(
                visible = isSearchActive,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search your countdowns & notes...", color = Color(0xFF7E889B)) },
                        singleLine = true,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = "Search",
                                tint = Color(0xFFE5C07B),
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotBlank()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(
                                        imageVector = Icons.Outlined.Close,
                                        contentDescription = "Clear",
                                        tint = Color(0xFF9FA6B6),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFE5C07B),
                            unfocusedBorderColor = Color(0xFF262C3D),
                            focusedContainerColor = Color(0xFF141720),
                            unfocusedContainerColor = Color(0xFF141720),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Category Chips Row
            CategoryChipRow(
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it },
                accentColor = Color(0xFFE5C07B),
                includeAll = true,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(Modifier.height(14.dp))

            // Countdowns Content List
            if (filteredItems.isEmpty()) {
                EmptyCountdownsState(
                    searchQuery = searchQuery,
                    onCreateClick = {
                        editingItem = null
                        isCreateSheetOpen = true
                    }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        // Section Header / Count
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ACTIVE MILESTONES (${filteredItems.size})",
                                style = MaterialTheme.typography.labelSmall,
                                letterSpacing = 1.5.sp,
                                color = Color(0xFF7E889B),
                                fontWeight = FontWeight.Bold
                            )

                            if (sortFurthestFirst) {
                                Text(
                                    text = "Furthest First",
                                    fontSize = 11.sp,
                                    color = Color(0xFFE5C07B)
                                )
                            }
                        }
                    }

                    items(filteredItems, key = { it.id }) { item ->
                        Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                            ArtisticCountdownCard(
                                item = item,
                                currentTimeMillis = currentTimeMillis,
                                onClick = { viewingDetailItem = item },
                                onTogglePin = { onTogglePin(item.id) },
                                onShare = { shareCountdownText(context, item) }
                            )
                        }
                    }

                    item {
                        Spacer(Modifier.height(72.dp))
                    }
                }
            }
        }
    }

    // Detail Modal Sheet
    viewingDetailItem?.let { item ->
        CountdownDetailSheet(
            item = item,
            currentTimeMillis = currentTimeMillis,
            onDismiss = { viewingDetailItem = null },
            onEdit = {
                viewingDetailItem = null
                editingItem = item
            },
            onDelete = {
                onDeleteCountdown(item.id)
                viewingDetailItem = null
            },
            onTogglePin = { onTogglePin(item.id) }
        )
    }

    // Create / Edit Modal Sheet
    if (isCreateSheetOpen || editingItem != null) {
        EditCountdownSheet(
            initialItem = editingItem,
            currentTimeMillis = currentTimeMillis,
            onDismiss = {
                isCreateSheetOpen = false
                editingItem = null
            },
            onSave = { savedItem ->
                onSaveCountdown(savedItem)
                isCreateSheetOpen = false
                editingItem = null
            }
        )
    }
}

@Composable
private fun AppHeader(
    isSearchActive: Boolean,
    onToggleSearch: () -> Unit,
    sortFurthestFirst: Boolean,
    onToggleSort: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "CHRONOS",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.5.sp,
                    color = Color.White
                )
                Spacer(Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE5C07B))
                )
            }
            Spacer(Modifier.height(2.dp))
            Text(
                text = "Precision Countdown & Milestones",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF8E95A5),
                letterSpacing = 0.5.sp
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onToggleSearch,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (isSearchActive) Color(0xFF262C3D) else Color(0xFF141720))
            ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "Search",
                    tint = if (isSearchActive) Color(0xFFE5C07B) else Color(0xFF9FA6B6),
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(Modifier.width(8.dp))

            IconButton(
                onClick = onToggleSort,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (sortFurthestFirst) Color(0xFF262C3D) else Color(0xFF141720))
            ) {
                Icon(
                    imageVector = Icons.Outlined.FilterList,
                    contentDescription = "Sort",
                    tint = if (sortFurthestFirst) Color(0xFFE5C07B) else Color(0xFF9FA6B6),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun EmptyCountdownsState(
    searchQuery: String,
    onCreateClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF161922))
                    .border(1.dp, Color(0xFF282E42), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Timer,
                    contentDescription = "No Countdowns",
                    tint = Color(0xFFE5C07B),
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text = if (searchQuery.isNotBlank()) "No Matching Milestones" else "No Countdowns Yet",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = if (searchQuery.isNotBlank()) {
                    "Try refining your search term or selecting a different category."
                } else {
                    "Begin tracking upcoming eclipses, launches, trips, or milestones with artistic precision."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF8E95A5),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 20.sp
            )

            if (searchQuery.isBlank()) {
                Spacer(Modifier.height(24.dp))
                androidx.compose.material3.Button(
                    onClick = onCreateClick,
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE5C07B),
                        contentColor = Color(0xFF141310)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Craft Your First Countdown",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

private fun shareCountdownText(context: Context, item: CountdownItem) {
    val remaining = item.calculateRemaining()
    val text = "\"${item.title}\" — ${remaining.days} days, ${remaining.hours} hours remaining until ${TimeUtils.formatEditorialDate(item.targetEpochMillis)}."
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, "Share Countdown"))
}
