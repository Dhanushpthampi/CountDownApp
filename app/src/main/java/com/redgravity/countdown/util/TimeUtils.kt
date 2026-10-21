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
        val hours = (diff / (1000L * 60 * 60)) % 24

        if (days == 0L) return if (hours == 0L) "In a few minutes" else "Today"
        if (days == 1L) return "Tomorrow"
        if (days < 7L) return "In $days days"
        if (days < 31L) return "In ${days / 7} week${if (days / 7 > 1L) "s" else ""}"

        // Calendar-accurate months + leftover days
        val tempMonth = java.util.Calendar.getInstance().apply { timeInMillis = currentMillis }
        var months = 0L
        while (true) {
            tempMonth.add(java.util.Calendar.MONTH, 1)
            if (tempMonth.timeInMillis > targetMillis) break
            months++
        }
        if (months <= 0L) return "In ${days / 7} weeks"

        val afterMonthsCal = java.util.Calendar.getInstance().apply {
            timeInMillis = currentMillis
            add(java.util.Calendar.MONTH, months.toInt())
        }
        val remDays = ((targetMillis - afterMonthsCal.timeInMillis) / (1000L * 60 * 60 * 24)).coerceAtLeast(0L)

        // Check for years
        val years = months / 12
        if (years >= 1) {
            val remMonths = months % 12
            return if (remMonths > 0)
                "In $years ${if (years == 1L) "year" else "years"} $remMonths ${if (remMonths == 1L) "month" else "months"}"
            else
                "In $years ${if (years == 1L) "year" else "years"}"
        }

        return if (remDays > 0)
            "In $months ${if (months == 1L) "month" else "months"} $remDays ${if (remDays == 1L) "day" else "days"}"
        else
            "In $months ${if (months == 1L) "month" else "months"}"
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
