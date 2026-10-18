package com.redgravity.countdown.model

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

data class CountdownVisualTheme(
    val id: String,
    val name: String,
    val description: String,
    val accentColor: Color,
    val secondaryAccent: Color,
    val surfaceGradientStart: Color,
    val surfaceGradientEnd: Color,
    val cardBackground: Color,
    val cardBorder: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val glanceAccentHex: Long,
    val glanceBackgroundHex: Long
) {
    fun gradientBrush(): Brush {
        return Brush.linearGradient(
            colors = listOf(surfaceGradientStart, surfaceGradientEnd)
        )
    }

    companion object {
        val OBSIDIAN_GOLD = CountdownVisualTheme(
            id = "obsidian_gold",
            name = "Obsidian & Gold",
            description = "Champagne gold against deep celestial black",
            accentColor = Color(0xFFE5C07B),
            secondaryAccent = Color(0xFFD4A359),
            surfaceGradientStart = Color(0xFF1E1B18),
            surfaceGradientEnd = Color(0xFF121110),
            cardBackground = Color(0xFF1A1816),
            cardBorder = Color(0xFF3D362A),
            textPrimary = Color(0xFFFBF8F1),
            textSecondary = Color(0xFFB8ADA1),
            glanceAccentHex = 0xFFE5C07BL,
            glanceBackgroundHex = 0xFF141312L
        )

        val AURORA_CYAN = CountdownVisualTheme(
            id = "aurora_cyan",
            name = "Aurora Cyan",
            description = "Luminous starlight teal in oceanic night",
            accentColor = Color(0xFF4EE2C0),
            secondaryAccent = Color(0xFF38B2AC),
            surfaceGradientStart = Color(0xFF0F1E24),
            surfaceGradientEnd = Color(0xFF0A1318),
            cardBackground = Color(0xFF0F1B20),
            cardBorder = Color(0xFF1E3A42),
            textPrimary = Color(0xFFE6FAF6),
            textSecondary = Color(0xFF8DAEB5),
            glanceAccentHex = 0xFF4EE2C0L,
            glanceBackgroundHex = 0xFF0D171CL
        )

        val ROSE_NOIR = CountdownVisualTheme(
            id = "rose_noir",
            name = "Rose Noir",
            description = "Subtle blush quartz over midnight velvet",
            accentColor = Color(0xFFF49AC1),
            secondaryAccent = Color(0xFFD9779F),
            surfaceGradientStart = Color(0xFF24151B),
            surfaceGradientEnd = Color(0xFF140D10),
            cardBackground = Color(0xFF1E1116),
            cardBorder = Color(0xFF45222E),
            textPrimary = Color(0xFFFDF2F6),
            textSecondary = Color(0xFFC49EA8),
            glanceAccentHex = 0xFFF49AC1L,
            glanceBackgroundHex = 0xFF180E13L
        )

        val EMERALD_SAGE = CountdownVisualTheme(
            id = "emerald_sage",
            name = "Emerald & Sage",
            description = "Botanical moss and radiant jade mist",
            accentColor = Color(0xFF56D695),
            secondaryAccent = Color(0xFF2EAA68),
            surfaceGradientStart = Color(0xFF112119),
            surfaceGradientEnd = Color(0xFF0A140F),
            cardBackground = Color(0xFF111D16),
            cardBorder = Color(0xFF234431),
            textPrimary = Color(0xFFEAF9F1),
            textSecondary = Color(0xFF8DBBA0),
            glanceAccentHex = 0xFF56D695L,
            glanceBackgroundHex = 0xFF0D1712L
        )

        val SOLAR_AMBER = CountdownVisualTheme(
            id = "solar_amber",
            name = "Solar Amber",
            description = "Warm twilight bronze and glowing honey",
            accentColor = Color(0xFFFF9E44),
            secondaryAccent = Color(0xFFE07A1E),
            surfaceGradientStart = Color(0xFF26190F),
            surfaceGradientEnd = Color(0xFF140E0A),
            cardBackground = Color(0xFF20150D),
            cardBorder = Color(0xFF4A301C),
            textPrimary = Color(0xFFFFF6EE),
            textSecondary = Color(0xFFC7A792),
            glanceAccentHex = 0xFFFF9E44L,
            glanceBackgroundHex = 0xFF18100AL
        )

        val AMETHYST_DUSK = CountdownVisualTheme(
            id = "amethyst_dusk",
            name = "Amethyst Dusk",
            description = "Deep violet atmosphere and lilac luminescence",
            accentColor = Color(0xFFB794F6),
            secondaryAccent = Color(0xFF9061E8),
            surfaceGradientStart = Color(0xFF1E142B),
            surfaceGradientEnd = Color(0xFF100B17),
            cardBackground = Color(0xFF1A1124),
            cardBorder = Color(0xFF3D2754),
            textPrimary = Color(0xFFF7F3FD),
            textSecondary = Color(0xFFB2A0C7),
            glanceAccentHex = 0xFFB794F6L,
            glanceBackgroundHex = 0xFF140D1DL
        )

        val TERRACOTTA_DUNE = CountdownVisualTheme(
            id = "terracotta_dune",
            name = "Terracotta Dune",
            description = "Earthy Venetian rust with warm clay notes",
            accentColor = Color(0xFFE07A5F),
            secondaryAccent = Color(0xFFC2593E),
            surfaceGradientStart = Color(0xFF241613),
            surfaceGradientEnd = Color(0xFF140D0C),
            cardBackground = Color(0xFF1E1310),
            cardBorder = Color(0xFF482922),
            textPrimary = Color(0xFFFAF2F0),
            textSecondary = Color(0xFFC5A39B),
            glanceAccentHex = 0xFFE07A5FL,
            glanceBackgroundHex = 0xFF18100DL
        )

        val MONO_TITANIUM = CountdownVisualTheme(
            id = "mono_titanium",
            name = "Mono Titanium",
            description = "Pure architectural carbon and crisp platinum",
            accentColor = Color(0xFFE2E8F0),
            secondaryAccent = Color(0xFF94A3B8),
            surfaceGradientStart = Color(0xFF1E2229),
            surfaceGradientEnd = Color(0xFF0F1115),
            cardBackground = Color(0xFF171920),
            cardBorder = Color(0xFF333846),
            textPrimary = Color(0xFFFFFFFF),
            textSecondary = Color(0xFFA0AAB8),
            glanceAccentHex = 0xFFE2E8F0L,
            glanceBackgroundHex = 0xFF13151BL
        )

        val ALL_THEMES = listOf(
            OBSIDIAN_GOLD,
            AURORA_CYAN,
            ROSE_NOIR,
            EMERALD_SAGE,
            SOLAR_AMBER,
            AMETHYST_DUSK,
            TERRACOTTA_DUNE,
            MONO_TITANIUM
        )

        fun fromId(id: String?): CountdownVisualTheme {
            return ALL_THEMES.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: OBSIDIAN_GOLD
        }
    }
}
