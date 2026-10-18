package com.redgravity.countdown.model

import java.util.UUID

data class CountdownItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val targetEpochMillis: Long,
    val createdEpochMillis: Long = System.currentTimeMillis(),
    val category: CountdownCategory = CountdownCategory.MILESTONE,
    val themeId: String = CountdownVisualTheme.OBSIDIAN_GOLD.id,
    val cardStyle: CountdownCardStyle = CountdownCardStyle.ARCHITECTURAL,
    val isPinned: Boolean = false,
    val updatedAtEpochMillis: Long = System.currentTimeMillis()
) {
    val visualTheme: CountdownVisualTheme
        get() = CountdownVisualTheme.fromId(themeId)

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

        val totalSpan = (targetEpochMillis - createdEpochMillis).coerceAtLeast(1L)
        val elapsed = (currentTimeMillis - createdEpochMillis).coerceAtLeast(0L)
        val progress = if (isFinished) 1.0f else (elapsed.toFloat() / totalSpan.toFloat()).coerceIn(0.0f, 1.0f)

        return CountdownTimeRemaining(
            days = days,
            hours = hours,
            minutes = minutes,
            seconds = seconds,
            totalSeconds = totalSeconds,
            totalDays = totalDays,
            totalWeeks = totalWeeks,
            isFinished = isFinished,
            progressRatio = progress
        )
    }
}
