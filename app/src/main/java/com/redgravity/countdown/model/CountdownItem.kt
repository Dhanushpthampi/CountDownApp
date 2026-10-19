package com.redgravity.countdown.model

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
        val days = positiveRemaining / (1000L * 60 * 60 * 24)
        val hours = (positiveRemaining / (1000L * 60 * 60)) % 24
        val minutes = (positiveRemaining / (1000L * 60)) % 60
        val seconds = (positiveRemaining / 1000L) % 60

        val totalDays = days
        val totalWeeks = days / 7

        // Calculate months and years remaining
        val months = totalDays / 30
        val years = totalDays / 365

        // Progress: use startEpochMillis if set, otherwise use createdEpochMillis
        val spanStart = startEpochMillis ?: createdEpochMillis
        val hasProgress = startEpochMillis != null
        val totalSpan = (targetEpochMillis - spanStart).coerceAtLeast(1L)
        val elapsed = (currentTimeMillis - spanStart).coerceAtLeast(0L)
        val progress = if (!hasProgress) 0f
            else if (isFinished) 1.0f
            else (elapsed.toFloat() / totalSpan.toFloat()).coerceIn(0.0f, 1.0f)

        return CountdownTimeRemaining(
            days = days,
            hours = hours,
            minutes = minutes,
            seconds = seconds,
            totalSeconds = totalSeconds,
            totalDays = totalDays,
            totalWeeks = totalWeeks,
            totalMonths = months,
            totalYears = years,
            isFinished = isFinished,
            progressRatio = progress,
            hasProgress = hasProgress
        )
    }
}
