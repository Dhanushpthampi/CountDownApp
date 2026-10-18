package com.redgravity.countdown.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object TimeUtils {

    fun formatEditorialDate(epochMillis: Long): String {
        return SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault()).format(Date(epochMillis))
    }

    fun formatShortDate(epochMillis: Long): String {
        return SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(epochMillis))
    }

    fun formatCompactDate(epochMillis: Long): String {
        return SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(epochMillis))
    }

    fun formatTime(epochMillis: Long): String {
        return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(epochMillis))
    }

    fun formatRelativeStatus(targetMillis: Long, currentMillis: Long = System.currentTimeMillis()): String {
        val diff = targetMillis - currentMillis
        if (diff <= 0) return "Arrived"

        val days = diff / (1000L * 60 * 60 * 24)
        return when {
            days == 0L -> "Today"
            days == 1L -> "Tomorrow"
            days < 7L -> "In $days days"
            days < 30L -> "In ${days / 7} weeks"
            days < 365L -> "In ${days / 30} months"
            else -> "In ${(days / 365.0).let { String.format(Locale.US, "%.1f", it) }} years"
        }
    }

    fun createPresetCalendar(daysOffset: Int = 0, monthsOffset: Int = 0, yearsOffset: Int = 0): Calendar {
        return Calendar.getInstance().apply {
            if (daysOffset != 0) add(Calendar.DAY_OF_YEAR, daysOffset)
            if (monthsOffset != 0) add(Calendar.MONTH, monthsOffset)
            if (yearsOffset != 0) add(Calendar.YEAR, yearsOffset)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }
}
