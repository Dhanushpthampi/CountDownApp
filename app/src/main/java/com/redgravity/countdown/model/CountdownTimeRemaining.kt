package com.redgravity.countdown.model

data class CountdownTimeRemaining(
    val days: Long,
    val hours: Long,
    val minutes: Long,
    val seconds: Long,
    val totalSeconds: Long,
    val totalDays: Long,
    val totalWeeks: Long,
    val isFinished: Boolean,
    val progressRatio: Float // 0.0f (just created) to 1.0f (reached target)
) {
    val formattedDays: String
        get() = days.toString()

    val formattedHours: String
        get() = hours.toString().padStart(2, '0')

    val formattedMinutes: String
        get() = minutes.toString().padStart(2, '0')

    val formattedSeconds: String
        get() = seconds.toString().padStart(2, '0')

    val daysLabel: String
        get() = if (days == 1L) "DAY" else "DAYS"

    val shortSummary: String
        get() = when {
            isFinished -> "Arrived"
            days > 0 -> "$days $daysLabel ${hours}h"
            hours > 0 -> "${hours}h ${minutes}m"
            else -> "${minutes}m ${seconds}s"
        }
}
