package com.redgravity.countdown.model

enum class CountdownCardStyle(
    val id: String,
    val displayName: String,
    val description: String
) {
    ARCHITECTURAL("architectural", "Architectural", "Clean editorial layout with crisp typography and subtle hairline dividers"),
    NEO_GLASS("neo_glass", "Atmospheric Glass", "Soft glowing gradient mesh with translucent frosted glass card"),
    RADIAL_PROGRESS("radial_progress", "Chronos Arc", "Dynamic circular progress ring measuring elapsed journey towards destination"),
    BOLD_NUMERIC("bold_numeric", "Editorial Mono", "Striking high-impact typography with prominent tabular numerals");

    companion object {
        fun fromId(id: String): CountdownCardStyle {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: ARCHITECTURAL
        }
    }
}
