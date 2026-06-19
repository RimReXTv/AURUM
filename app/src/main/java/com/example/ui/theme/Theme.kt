package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val AurumDarkColorScheme = darkColorScheme(
    primary = AurumGold,
    secondary = AurumBronze,
    tertiary = NeonSliver,
    background = ObsidianDark,
    surface = DarkCardSurface,
    onPrimary = ObsidianDark,
    onSecondary = ObsidianDark,
    onBackground = AurumLight,
    onSurface = AurumLight,
    surfaceVariant = BorderSlate,
    error = ErrorRuby
)

@Composable
fun AurumGridTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = ObsidianDark.toArgb()
            window.navigationBarColor = ObsidianDark.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = AurumDarkColorScheme,
        typography = Typography,
        content = content
    )
}
