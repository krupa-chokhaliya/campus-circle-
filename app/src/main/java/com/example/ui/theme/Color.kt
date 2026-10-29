package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// CampusCircle Visual Identity Colors
val CampusBlack = Color(0xFF09070C)
val CampusSurface = Color(0xFF17141C)
val CampusSurfaceElevated = Color(0xFF24202A)
val CampusSurfaceBorder = Color(0x33FF80B5)
val CampusCardBorder = Color(0x22FFFFFF)

val CampusPrimaryPink = Color(0xFFF72585)
val CampusSecondaryPink = Color(0xFFFF80B5)
val CampusPurpleAccent = Color(0xFF9B5DE5)
val CampusPurpleDeep = Color(0xFF560BAD)
val CampusCyanAccent = Color(0xFF4CC9F0)

val CampusTextPrimary = Color(0xFFF8F5FA)
val CampusTextSecondary = Color(0xFFB9B2C1)
val CampusTextMuted = Color(0xFF7E7688)

val CampusError = Color(0xFFEF476F)
val CampusSuccess = Color(0xFF06D6A0)
val CampusWarning = Color(0xFFFFD166)

// Light Theme Variants
val CampusLightBackground = Color(0xFFFBF8FD)
val CampusLightSurface = Color(0xFFFFFFFF)
val CampusLightSurfaceElevated = Color(0xFFF3EDF7)
val CampusLightTextPrimary = Color(0xFF1B181F)
val CampusLightTextSecondary = Color(0xFF5D5765)
val CampusLightBorder = Color(0x20F72585)

// Gradients
val CampusBrandGradient = Brush.horizontalGradient(
    colors = listOf(CampusPrimaryPink, CampusPurpleAccent)
)

val CampusAccentGradient = Brush.linearGradient(
    colors = listOf(CampusPrimaryPink, CampusSecondaryPink, CampusPurpleAccent)
)

val CampusCardGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF201A29), Color(0xFF14111A))
)

val CampusHeroGradient = Brush.radialGradient(
    colors = listOf(Color(0x35F72585), Color(0x0009070C)),
    radius = 600f
)
