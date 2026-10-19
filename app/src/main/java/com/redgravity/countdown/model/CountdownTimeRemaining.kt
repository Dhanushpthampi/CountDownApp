package com.redgravity.countdown.model

data class CountdownTimeRemaining(
    val days: Long,
    val hours: Long,
    val minutes: Long,
    val seconds: Long,
    val totalSeconds: Long,
    val totalDays: Long,
    val totalWeeks: Long,
    val totalMonths: Long,
    val totalYears: Long,
    val isFinished: Boolean,
    val progressRatio: Float, // 0.0f (just started) to 1.0f (reached target), 0 if no start time
    val hasProgress: Boolean   // true only when startEpochMillis is set
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
        get() = if (days == 1L) "day" else "days"

    // Smart label: shows years if >= 1 year, months if >= 1 month, else days
    val smartValue: Long
        get() = when {
            isFinished -> 0L
            totalYears >= 1 -> totalYears
            totalMonths >= 1 -> totalMonths
            else -> totalDays
        }

    val smartUnit: String
        get() = when {
            isFinished -> "days"
            totalYears >= 1 -> if (totalYears == 1L) "year" else "years"
            totalMonths >= 1 -> if (totalMonths == 1L) "month" else "months"
            else -> if (totalDays == 1L) "day" else "days"
        }

    val shortSummary: String
        get() = when {
            isFinished -> "Arrived"
            totalYears >= 1 -> "$totalYears yr${if (totalYears > 1) "s" else ""}"
            totalMonths >= 1 -> "$totalMonths mo"
            days > 0 -> "$days ${daysLabel}"
            hours > 0 -> "${hours}h ${minutes}m"
            else -> "${minutes}m ${seconds}s"
        }
}
