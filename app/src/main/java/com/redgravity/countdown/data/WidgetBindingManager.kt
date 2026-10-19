package com.redgravity.countdown.data

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.glance.appwidget.updateAll
import com.redgravity.countdown.CountdownWidget
import com.redgravity.countdown.CountdownWidgetReceiver
import com.redgravity.countdown.model.CountdownItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object WidgetBindingManager {

    private const val PREFS_NAME = "widget_bindings_prefs"
    private const val KEY_PREFIX = "widget_bound_countdown_"

    fun bindWidgetToCountdown(context: Context, appWidgetId: Int, countdownId: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString("$KEY_PREFIX$appWidgetId", countdownId).apply()
        updateAllWidgets(context)
    }

    fun getCountdownIdForWidget(context: Context, appWidgetId: Int): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString("$KEY_PREFIX$appWidgetId", null)
    }

    fun removeWidgetBinding(context: Context, appWidgetId: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove("$KEY_PREFIX$appWidgetId").apply()
    }

    /**
     * Strict lookup: only returns a countdown if THIS widget has an explicit binding.
     * Returns null if not bound. This prevents all unbound widgets from showing the same countdown.
     */
    fun getCountdownForWidgetStrict(context: Context, appWidgetId: Int): CountdownItem? {
        val repository = CountdownRepository.getInstance(context)
        val boundId = getCountdownIdForWidget(context, appWidgetId)
        if (boundId != null) {
            val item = repository.getById(boundId)
            if (item != null) return item
        }
        // Explicit binding not found — return null so widget shows "tap to configure"
        return null
    }

    /**
     * Deprecated fallback that returns first available countdown.
     * Use getCountdownForWidgetStrict() instead to avoid multiple widgets showing the same item.
     */
    @Deprecated("Use getCountdownForWidgetStrict instead")
    fun getCountdownForWidget(context: Context, appWidgetId: Int): CountdownItem? {
        return getCountdownForWidgetStrict(context, appWidgetId)
    }

    fun updateAllWidgets(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                CountdownWidget().updateAll(context)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun canRequestPinWidget(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val appWidgetManager = context.getSystemService(AppWidgetManager::class.java)
            return appWidgetManager?.isRequestPinAppWidgetSupported == true
        }
        return false
    }

    fun requestPinWidget(context: Context, countdownId: String): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val appWidgetManager = context.getSystemService(AppWidgetManager::class.java) ?: return false
            if (!appWidgetManager.isRequestPinAppWidgetSupported) return false

            val provider = ComponentName(context, CountdownWidgetReceiver::class.java)
            val successIntent = Intent(context, CountdownWidgetReceiver::class.java).apply {
                action = "com.redgravity.countdown.ACTION_WIDGET_PINNED"
                putExtra("countdown_id", countdownId)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                countdownId.hashCode(),
                successIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val extras = Bundle().apply {
                putString("countdown_id", countdownId)
            }

            return appWidgetManager.requestPinAppWidget(provider, extras, pendingIntent)
        }
        return false
    }
}
