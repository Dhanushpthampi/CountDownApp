package com.redgravity.countdown.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = GoldChampagne,
    onPrimary = Color(0xFF1E1A11),
    primaryContainer = Color(0xFF3B321F),
    onPrimaryContainer = Color(0xFFFBEFD6),
    secondary = StarlightCyan,
    onSecondary = Color(0xFF0A221E),
    secondaryContainer = Color(0xFF163E37),
    onSecondaryContainer = Color(0xFFD6FAF2),
    tertiary = RoseQuartz,
    onTertiary = Color(0xFF330F20),
    background = SpaceObsidian,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceDarkElevated,
    onSurfaceVariant = TextSecondaryDark,
    outline = SurfaceDarkBorder
)

private val LightColorScheme = lightColorScheme(
    primary = GoldWarm,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFDF4E3),
    onPrimaryContainer = Color(0xFF422C0A),
    secondary = Color(0xFF1E7E6E),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0F7F3),
    onSecondaryContainer = Color(0xFF003830),
    tertiary = Color(0xFFB54474),
    onTertiary = Color.White,
    background = SurfaceLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceLightElevated,
    onSurface = TextPrimaryLight,
    surfaceVariant = Color(0xFFEFF2F7),
    onSurfaceVariant = TextSecondaryLight,
    outline = SurfaceLightBorder
)

@Composable
fun CountDownTheme(
    darkTheme: Boolean = true, // Default to stunning dark luxury aesthetics
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}