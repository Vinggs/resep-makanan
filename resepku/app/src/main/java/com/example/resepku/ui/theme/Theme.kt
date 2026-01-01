package com.example.resepku.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.ui.graphics.Color

// Skema Warna Gelap
private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    secondary = DarkSecondary,
    background = DarkBackground
)

// Skema Warna Terang (Yang utama kita pakai)
private val LightColorScheme = lightColorScheme(
    primary = OrangePrimary,
    onPrimary = WhiteSurface,
    secondary = OrangeSecondary,
    tertiary = GreenAccent,
    background = KremBackground,
    surface = WhiteSurface,
    onSurface = Color.Black
)

@Composable
fun ResepKuTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color di-false-kan dulu agar warna kita yang dipakai
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Mengubah warna status bar di atas HP
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}