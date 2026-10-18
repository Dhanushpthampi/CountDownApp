package com.redgravity.countdown

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.redgravity.countdown.data.CountdownRepository
import com.redgravity.countdown.ui.screens.CountdownHomeScreen
import com.redgravity.countdown.ui.theme.CountDownTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var repository: CountdownRepository
    private var initialCountdownId by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        repository = CountdownRepository.getInstance(applicationContext)
        initialCountdownId = intent?.getStringExtra("countdown_id")

        setContent {
            val scope = rememberCoroutineScope()
            val countdowns by repository.countdowns.collectAsState()

            CountDownTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF0C0E14)
                ) {
                    CountdownHomeScreen(
                        countdowns = countdowns,
                        initialSelectedCountdownId = initialCountdownId,
                        onSaveCountdown = { item ->
                            scope.launch {
                                repository.save(item)
                            }
                        },
                        onDeleteCountdown = { id ->
                            scope.launch {
                                repository.delete(id)
                            }
                        },
                        onTogglePin = { id ->
                            scope.launch {
                                repository.togglePin(id)
                            }
                        }
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        initialCountdownId = intent.getStringExtra("countdown_id")
    }
}
