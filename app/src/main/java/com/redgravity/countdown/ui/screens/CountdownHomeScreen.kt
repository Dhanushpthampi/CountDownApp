package com.redgravity.countdown.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.redgravity.countdown.model.CountdownCategory
import com.redgravity.countdown.model.CountdownItem
import com.redgravity.countdown.ui.components.CategoryChipRow
import com.redgravity.countdown.ui.components.MaterialCountdownCard
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountdownHomeScreen(
    countdowns: List<CountdownItem>,
    initialSelectedCountdownId: String? = null,
    onSaveCountdown: (CountdownItem) -> Unit,
    onDeleteCountdown: (String) -> Unit
) {
    // Live clock tick
    var currentTimeMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            currentTimeMillis = System.currentTimeMillis()
            delay(1000)
        }
    }

    var selectedCategory by remember { mutableStateOf(CountdownCategory.ALL) }

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

    // Keep viewingDetailItem in sync
    LaunchedEffect(countdowns, initialSelectedCountdownId) {
        if (initialSelectedCountdownId != null && viewingDetailItem == null) {
            viewingDetailItem = countdowns.firstOrNull { it.id == initialSelectedCountdownId }
        } else if (viewingDetailItem != null) {
            viewingDetailItem = countdowns.firstOrNull { it.id == viewingDetailItem!!.id }
        }
    }

    // Filter
    val filteredItems = remember(countdowns, selectedCategory) {
        countdowns.filter { item ->
            selectedCategory == CountdownCategory.ALL || item.category == selectedCategory
        }
    }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "Countdowns",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Normal
                    )
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                ),
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    editingItem = null
                    isCreateSheetOpen = true
                },
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.HourglassEmpty,
                        contentDescription = "New Countdown"
                    )
                },
                text = { Text("New countdown") },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Category filter chips
            CategoryChipRow(
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it },
                includeAll = true,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            Spacer(Modifier.height(8.dp))

            if (filteredItems.isEmpty()) {
                EmptyCountdownsState(
                    hasAnyCountdowns = countdowns.isNotEmpty(),
                    onCreateClick = {
                        editingItem = null
                        isCreateSheetOpen = true
                    }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 96.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredItems, key = { it.id }) { item ->
                        MaterialCountdownCard(
                            item = item,
                            currentTimeMillis = currentTimeMillis,
                            onClick = { viewingDetailItem = item }
                        )
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
            }
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
private fun EmptyCountdownsState(
    hasAnyCountdowns: Boolean,
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
            Icon(
                imageVector = Icons.Outlined.HourglassEmpty,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(72.dp)
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = if (hasAnyCountdowns) "No countdowns in this category" else "No countdowns yet",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = if (hasAnyCountdowns) {
                    "Try selecting a different category filter."
                } else {
                    "Track upcoming events, milestones, and goals."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = androidx.compose.ui.unit.TextUnit(
                    22f,
                    androidx.compose.ui.unit.TextUnitType.Sp
                )
            )

            if (!hasAnyCountdowns) {
                Spacer(Modifier.height(28.dp))
                FilledTonalButton(onClick = onCreateClick) {
                    Icon(
                        imageVector = Icons.Outlined.HourglassEmpty,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Create your first countdown")
                }
            }
        }
    }
}
