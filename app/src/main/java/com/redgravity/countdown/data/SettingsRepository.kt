package com.redgravity.countdown.data

import android.content.Context
import android.content.SharedPreferences
import com.redgravity.countdown.App

object SettingsRepository {
    private const val PREFS_NAME = "app_settings"
    private const val KEY_DARK_MODE_OVERRIDE = "dark_mode_override"
    private const val KEY_USE_DARK_MODE = "use_dark_mode"
    private const val KEY_SHOW_PROGRESS_RING = "show_progress_ring"
    private const val KEY_SHOW_DETAILED_BREAKDOWN = "show_detailed_breakdown"

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var darkModeOverride: Boolean
        get() = prefs(App.instance).getBoolean(KEY_DARK_MODE_OVERRIDE, false)
        set(value) = prefs(App.instance).edit().putBoolean(KEY_DARK_MODE_OVERRIDE, value).apply()

    var useDarkMode: Boolean
        get() = prefs(App.instance).getBoolean(KEY_USE_DARK_MODE, true)
        set(value) = prefs(App.instance).edit().putBoolean(KEY_USE_DARK_MODE, value).apply()

    var showProgressRing: Boolean
        get() = prefs(App.instance).getBoolean(KEY_SHOW_PROGRESS_RING, true)
        set(value) = prefs(App.instance).edit().putBoolean(KEY_SHOW_PROGRESS_RING, value).apply()

    var showDetailedBreakdown: Boolean
        get() = prefs(App.instance).getBoolean(KEY_SHOW_DETAILED_BREAKDOWN, true)
        set(value) = prefs(App.instance).edit().putBoolean(KEY_SHOW_DETAILED_BREAKDOWN, value).apply()
}
