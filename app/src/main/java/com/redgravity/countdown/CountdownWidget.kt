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
import com.redgravity.countdown.util.TimeUtils

class CountdownWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // IMPORTANT: always resolve the appWidgetId from glanceId for proper per-widget binding
        val appWidgetManager = GlanceAppWidgetManager(context)
        val appWidgetId = try {
            appWidgetManager.getAppWidgetId(id)
        } catch (e: Exception) {
            -1
        }

        // Look up the countdown bound specifically to THIS widget instance
        val countdownItem: CountdownItem? = if (appWidgetId != -1) {
            WidgetBindingManager.getCountdownForWidgetStrict(context, appWidgetId)
        } else {
            null
        }

        provideContent {
            val bgColor = Color(0xFF1C1B1F)       // M3 dark surface
            val primaryColor = Color(0xFF80CBFF)   // M3 dark primary
            val onSurfaceVariant = Color(0xFFC1C7CE)

            if (countdownItem == null) {
                val openIntent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                Box(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .cornerRadius(16.dp)
                        .background(bgColor)
                        .padding(16.dp)
                        .clickable(actionStartActivity(openIntent)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "⧗",
                            style = TextStyle(
                                color = ColorProvider(primaryColor),
                                fontSize = 28.sp
                            )
                        )
                        Spacer(GlanceModifier.height(8.dp))
                        Text(
                            text = "Tap to choose a countdown",
                            style = TextStyle(
                                color = ColorProvider(onSurfaceVariant),
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                }
            } else {
                val remaining = countdownItem.calculateRemaining()
                val openIntent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    putExtra("countdown_id", countdownItem.id)
                }

                Box(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .cornerRadius(16.dp)
                        .background(bgColor)
                        .padding(16.dp)
                        .clickable(actionStartActivity(openIntent))
                ) {
                    Column(
                        modifier = GlanceModifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Category label
                        Text(
                            text = countdownItem.category.title.uppercase(),
                            style = TextStyle(
                                color = ColorProvider(primaryColor),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )

                        Spacer(GlanceModifier.height(4.dp))

                        // Title
                        Text(
                            text = countdownItem.title,
                            maxLines = 1,
                            style = TextStyle(
                                color = ColorProvider(Color.White),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )

                        Spacer(GlanceModifier.height(8.dp))

                        if (remaining.isFinished) {
                            Text(
                                text = "🎉 Milestone reached!",
                                style = TextStyle(
                                    color = ColorProvider(primaryColor),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        } else {
                            Column {
                                    Spacer(GlanceModifier.height(8.dp))
                                    Text(
                                        text = remaining.shortReadable,
                                        style = TextStyle(
                                            color = ColorProvider(primaryColor),
                                            fontSize = 30.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Text(
                                        text = "remaining",
                                        style = TextStyle(
                                            color = ColorProvider(onSurfaceVariant),
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                        }

                        Spacer(GlanceModifier.height(4.dp))

                        Text(
                            text = TimeUtils.formatCompactDate(countdownItem.targetEpochMillis),
                            style = TextStyle(
                                color = ColorProvider(onSurfaceVariant),
                                fontSize = 11.sp
                            )
                        )
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
            WidgetBindingManager.updateAllWidgets(context)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        // Clean up bindings when widgets are removed
        appWidgetIds.forEach { id ->
            WidgetBindingManager.removeWidgetBinding(context, id)
        }
    }
}
