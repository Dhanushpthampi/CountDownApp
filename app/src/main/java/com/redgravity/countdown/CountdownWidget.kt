package com.redgravity.countdown

import android.content.Context
import android.content.Intent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.redgravity.countdown.data.CountdownRepository
import com.redgravity.countdown.data.WidgetBindingManager
import com.redgravity.countdown.model.CountdownItem
import com.redgravity.countdown.model.CountdownVisualTheme
import com.redgravity.countdown.util.TimeUtils

class CountdownWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val appWidgetManager = GlanceAppWidgetManager(context)
        val appWidgetId = try {
            appWidgetManager.getAppWidgetId(id)
        } catch (e: Exception) {
            -1
        }

        val countdownItem: CountdownItem? = if (appWidgetId != -1) {
            WidgetBindingManager.getCountdownForWidget(context, appWidgetId)
        } else {
            val repo = CountdownRepository.getInstance(context)
            repo.getAll().firstOrNull { it.isPinned } ?: repo.getAll().firstOrNull()
        }

        provideContent {
            if (countdownItem == null) {
                // Empty state card
                val openIntent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                Box(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .cornerRadius(22.dp)
                        .background(Color(0xFF14161E))
                        .padding(16.dp)
                        .clickable(actionStartActivity(openIntent)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "CHRONOS",
                            style = TextStyle(
                                color = ColorProvider(Color(0xFFE5C07B)),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(GlanceModifier.height(4.dp))
                        Text(
                            text = "Tap to choose a countdown",
                            style = TextStyle(
                                color = ColorProvider(Color(0xFFB0B6C3)),
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                }
            } else {
                val theme = countdownItem.visualTheme
                val remaining = countdownItem.calculateRemaining()
                val openIntent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    putExtra("countdown_id", countdownItem.id)
                }

                Box(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .cornerRadius(22.dp)
                        .background(Color(theme.glanceBackgroundHex))
                        .padding(16.dp)
                        .clickable(actionStartActivity(openIntent))
                ) {
                    Column(
                        modifier = GlanceModifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Header row with category and relative label
                        Row(
                            modifier = GlanceModifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "✦  ${countdownItem.category.title.uppercase()}",
                                style = TextStyle(
                                    color = ColorProvider(Color(theme.glanceAccentHex)),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(GlanceModifier.defaultWeight())
                            Text(
                                text = TimeUtils.formatCompactDate(countdownItem.targetEpochMillis),
                                style = TextStyle(
                                    color = ColorProvider(Color(0xFF8E95A5)),
                                    fontSize = 11.sp
                                )
                            )
                        }

                        Spacer(GlanceModifier.height(6.dp))

                        // Title
                        Text(
                            text = countdownItem.title,
                            maxLines = 1,
                            style = TextStyle(
                                color = ColorProvider(Color.White),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )

                        Spacer(GlanceModifier.height(6.dp))

                        // Main Counter
                        if (remaining.isFinished) {
                            Text(
                                text = "MOMENT REACHED",
                                style = TextStyle(
                                    color = ColorProvider(Color(theme.glanceAccentHex)),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(GlanceModifier.height(2.dp))
                            Text(
                                text = "Milestone achieved",
                                style = TextStyle(
                                    color = ColorProvider(Color(0xFF9FA6B6)),
                                    fontSize = 12.sp
                                )
                            )
                        } else {
                            Row(
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Text(
                                    text = remaining.days.toString(),
                                    style = TextStyle(
                                        color = ColorProvider(Color(theme.glanceAccentHex)),
                                        fontSize = 38.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Spacer(GlanceModifier.width(6.dp))
                                Column {
                                    Text(
                                        text = remaining.daysLabel,
                                        style = TextStyle(
                                            color = ColorProvider(Color(0xFFD6DBE5)),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Text(
                                        text = "${remaining.formattedHours}h ${remaining.formattedMinutes}m remaining",
                                        style = TextStyle(
                                            color = ColorProvider(Color(0xFF8E95A5)),
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

class CountdownWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = CountdownWidget()

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == "com.redgravity.countdown.ACTION_WIDGET_PINNED") {
            val countdownId = intent.getStringExtra("countdown_id")
            if (countdownId != null) {
                // Refresh widgets
                WidgetBindingManager.updateAllWidgets(context)
            }
        }
    }
}
