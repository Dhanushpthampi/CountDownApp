package com.redgravity.countdown.model

enum class CountdownCategory(
    val id: String,
    val title: String,
    val subtitle: String
) {
    ALL("all", "All", "Every countdown"),
    MILESTONE("milestone", "Milestone", "Life and personal achievements"),
    CELEBRATION("celebration", "Celebration", "Special occasions and gatherings"),
    TRAVEL("travel", "Travel", "Journeys, flights, and getaways"),
    CAREER("career", "Career", "Work, releases, and deadlines"),
    GOAL("goal", "Goal", "Ambitions and targets"),
    CREATIVE("creative", "Creative", "Artistic projects and launches"),
    ASTRONOMY("astronomy", "Astronomy", "Eclipses, solstices, and cosmic events");

    companion object {
        fun fromId(id: String): CountdownCategory {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: MILESTONE
        }
    }
}
