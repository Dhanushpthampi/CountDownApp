package com.redgravity.countdown.model

enum class CountdownCardStyle(
    val id: String,
    val displayName: String,
    val description: String
) {
    STANDARD("standard", "Standard", "Clean Material card with days/hours/mins/secs breakdown"),
    COMPACT("compact", "Compact", "Minimal single-line display with smart time unit"),
    PROGRESS_BAR("progress_bar", "Progress Bar", "Linear progress bar showing journey completion"),
    PROGRESS_CIRCLE("progress_circle", "Progress Ring", "Circular progress ring with time remaining");

    companion object {
        fun fromId(id: String): CountdownCardStyle {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: STANDARD
        }
    }
}
