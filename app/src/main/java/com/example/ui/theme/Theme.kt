package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val DarkCampusColorScheme = darkColorScheme(
    primary = CampusPrimaryPink,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF3E122A),
    onPrimaryContainer = CampusSecondaryPink,
    secondary = CampusPurpleAccent,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF28133E),
    onSecondaryContainer = Color(0xFFD6B4FC),
    tertiary = CampusSecondaryPink,
    onTertiary = CampusBlack,
    background = CampusBlack,
    onBackground = CampusTextPrimary,
    surface = CampusSurface,
    onSurface = CampusTextPrimary,
    surfaceVariant = CampusSurfaceElevated,
    onSurfaceVariant = CampusTextSecondary,
    outline = CampusSurfaceBorder,
    outlineVariant = CampusCardBorder,
    error = CampusError,
    onError = Color.White
)

val LightCampusColorScheme = lightColorScheme(
    primary = CampusPrimaryPink,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFD8E7),
    onPrimaryContainer = Color(0xFF3D001C),
    secondary = CampusPurpleAccent,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEEDCFF),
    onSecondaryContainer = Color(0xFF280053),
    tertiary = CampusPurpleDeep,
    onTertiary = Color.White,
    background = CampusLightBackground,
    onBackground = CampusLightTextPrimary,
    surface = CampusLightSurface,
    onSurface = CampusLightTextPrimary,
    surfaceVariant = CampusLightSurfaceElevated,
    onSurfaceVariant = CampusLightTextSecondary,
    outline = CampusLightBorder,
    outlineVariant = Color(0x15000000),
    error = CampusError,
    onError = Color.White
)

@Composable
fun CampusCircleTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkCampusColorScheme else LightCampusColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    CampusCircleTheme(darkTheme = darkTheme, content = content)
}
