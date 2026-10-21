package com.redgravity.countdown.model

import java.util.Calendar
import java.util.UUID

data class CountdownItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val targetEpochMillis: Long,
    val createdEpochMillis: Long = System.currentTimeMillis(),
    // Optional: if set, progress is calculated from startEpochMillis instead of createdEpochMillis
    val startEpochMillis: Long? = null,
    val category: CountdownCategory = CountdownCategory.MILESTONE,
    val cardStyle: CountdownCardStyle = CountdownCardStyle.STANDARD,
    val updatedAtEpochMillis: Long = System.currentTimeMillis()
) {
    fun calculateRemaining(currentTimeMillis: Long = System.currentTimeMillis()): CountdownTimeRemaining {
        val remainingMillis = targetEpochMillis - currentTimeMillis
        val isFinished = remainingMillis <= 0L
        val positiveRemaining = remainingMillis.coerceAtLeast(0L)

        val totalSeconds = positiveRemaining / 1000L
        val totalDays = positiveRemaining / (1000L * 60 * 60 * 24)
        val hours = (positiveRemaining / (1000L * 60 * 60)) % 24
        val minutes = (positiveRemaining / (1000L * 60)) % 60
        val seconds = (positiveRemaining / 1000L) % 60
        val totalWeeks = totalDays / 7

        // Accurate calendar-based month/year/remaining-days calculation
        val nowCal = Calendar.getInstance().apply { timeInMillis = currentTimeMillis }
        val targetCal = Calendar.getInstance().apply { timeInMillis = targetEpochMillis }

        var calYears = 0L
        var calMonths = 0L
        var calRemDays = 0L

        if (!isFinished) {
            // Count full years
            val y = (targetCal.get(Calendar.YEAR) - nowCal.get(Calendar.YEAR)).toLong()
            val tempAfterYears = Calendar.getInstance().apply {
                timeInMillis = currentTimeMillis
                add(Calendar.YEAR, y.toInt())
            }
            val effectiveYears = if (tempAfterYears.timeInMillis > targetEpochMillis) y - 1 else y
            calYears = effectiveYears.coerceAtLeast(0L)

            // Advance by years, then count full months
            val afterYearsCal = Calendar.getInstance().apply {
                timeInMillis = currentTimeMillis
                add(Calendar.YEAR, calYears.toInt())
            }
            var m = 0L
            val tempMonth = Calendar.getInstance().apply { timeInMillis = afterYearsCal.timeInMillis }
            while (true) {
                tempMonth.add(Calendar.MONTH, 1)
                if (tempMonth.timeInMillis > targetEpochMillis) break
                m++
            }
            calMonths = m

            // Advance by months, remaining = days leftover
            val afterMonthsCal = Calendar.getInstance().apply {
                timeInMillis = afterYearsCal.timeInMillis
                add(Calendar.MONTH, calMonths.toInt())
            }
            calRemDays = ((targetEpochMillis - afterMonthsCal.timeInMillis) / (1000L * 60 * 60 * 24))
                .coerceAtLeast(0L)
        }

        // totalMonths = calYears*12 + calMonths (approximate, kept for backward compat)
        val totalMonths = calYears * 12 + calMonths
        val totalYears = calYears

        // Progress: use startEpochMillis if set, otherwise use createdEpochMillis
        val spanStart = startEpochMillis ?: createdEpochMillis
        val hasProgress = startEpochMillis != null
        val totalSpan = (targetEpochMillis - spanStart).coerceAtLeast(1L)
        val elapsed = (currentTimeMillis - spanStart).coerceAtLeast(0L)
        val progress = if (!hasProgress) 0f
            else if (isFinished) 1.0f
            else (elapsed.toFloat() / totalSpan.toFloat()).coerceIn(0.0f, 1.0f)

        return CountdownTimeRemaining(
            days = totalDays,
            hours = hours,
            minutes = minutes,
            seconds = seconds,
            totalSeconds = totalSeconds,
            totalDays = totalDays,
            totalWeeks = totalWeeks,
            totalMonths = totalMonths,
            totalYears = totalYears,
            calYears = calYears,
            calMonths = calMonths,
            calRemDays = calRemDays,
            isFinished = isFinished,
            progressRatio = progress,
            hasProgress = hasProgress
        )
    }
}
