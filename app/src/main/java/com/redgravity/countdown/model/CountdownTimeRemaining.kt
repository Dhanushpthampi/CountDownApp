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
    // Accurate calendar-based breakdown (years + months + leftover days)
    val calYears: Long,
    val calMonths: Long,
    val calRemDays: Long,
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
            calYears >= 1 -> calYears
            calMonths >= 1 -> calMonths
            else -> totalDays
        }

    val smartUnit: String
        get() = when {
            isFinished -> "days"
            calYears >= 1 -> if (calYears == 1L) "year" else "years"
            calMonths >= 1 -> if (calMonths == 1L) "month" else "months"
            else -> if (totalDays == 1L) "day" else "days"
        }

    /**
     * Human-readable countdown, e.g.:
     *   "2 years 3 months 5 days remaining"
     *   "4 months 10 days remaining"
     *   "18 days remaining"
     *   "6 hours 45 minutes remaining"
     *   "Arrived"
     */
    val readableRemaining: String
        get() {
            if (isFinished) return "Arrived"
            val parts = mutableListOf<String>()
            when {
                calYears >= 1 -> {
                    parts += "${calYears} ${if (calYears == 1L) "year" else "years"}"
                    if (calMonths > 0) parts += "${calMonths} ${if (calMonths == 1L) "month" else "months"}"
                    if (calRemDays > 0 && calYears < 2) parts += "${calRemDays} ${if (calRemDays == 1L) "day" else "days"}"
                }
                calMonths >= 1 -> {
                    parts += "${calMonths} ${if (calMonths == 1L) "month" else "months"}"
                    if (calRemDays > 0) parts += "${calRemDays} ${if (calRemDays == 1L) "day" else "days"}"
                }
                totalDays >= 1 -> {
                    parts += "${totalDays} ${if (totalDays == 1L) "day" else "days"}"
                }
                hours > 0 -> {
                    parts += "${hours} ${if (hours == 1L) "hour" else "hours"}"
                    if (minutes > 0) parts += "${minutes} ${if (minutes == 1L) "minute" else "minutes"}"
                }
                else -> {
                    parts += "${minutes} ${if (minutes == 1L) "minute" else "minutes"}"
                    if (seconds > 0) parts += "${seconds} ${if (seconds == 1L) "second" else "seconds"}"
                }
            }
            return parts.joinToString(" ") + " remaining"
        }

    /**
     * Shorter version, e.g. "2yr 3mo", "4mo 10d", "18 days", "6h 45m"
     */
    val shortReadable: String
        get() {
            if (isFinished) return "Arrived"
            return when {
                calYears >= 1 -> {
                    val yr = "${calYears}yr"
                    if (calMonths > 0) "$yr ${calMonths}mo" else yr
                }
                calMonths >= 1 -> {
                    val mo = "${calMonths}mo"
                    if (calRemDays > 0) "$mo ${calRemDays}d" else mo
                }
                totalDays >= 1 -> "${totalDays}d"
                hours > 0 -> "${hours}h ${minutes}m"
                else -> "${minutes}m ${seconds}s"
            }
        }

    val shortSummary: String
        get() = when {
            isFinished -> "Arrived"
            calYears >= 1 -> "${calYears} yr${if (calYears > 1) "s" else ""}"
            calMonths >= 1 -> "${calMonths} mo"
            days > 0 -> "$days ${daysLabel}"
            hours > 0 -> "${hours}h ${minutes}m"
            else -> "${minutes}m ${seconds}s"
        }
}

