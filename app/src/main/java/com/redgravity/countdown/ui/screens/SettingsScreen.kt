package com.redgravity.countdown.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.redgravity.countdown.data.SettingsRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            // Dark mode toggle
            var useDarkMode by remember { mutableStateOf(SettingsRepository.useDarkMode) }
            SwitchSettingItem(
                title = "Enable Dark Mode",
                checked = useDarkMode,
                onCheckedChange = {
                    useDarkMode = it
                    SettingsRepository.useDarkMode = it
                }
            )
            // Progress ring toggle
            var showRing by remember { mutableStateOf(SettingsRepository.showProgressRing) }
            SwitchSettingItem(
                title = "Show Progress Ring",
                checked = showRing,
                onCheckedChange = {
                    showRing = it
                    SettingsRepository.showProgressRing = it
                }
            )
            // Detailed breakdown toggle
            var detailed by remember { mutableStateOf(SettingsRepository.showDetailedBreakdown) }
            SwitchSettingItem(
                title = "Show Detailed Breakdown",
                checked = detailed,
                onCheckedChange = {
                    detailed = it
                    SettingsRepository.showDetailedBreakdown = it
                }
            )
        }
    }
}

@Composable
private fun SwitchSettingItem(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
