package com.duoc.aldemaraqua.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Tema opcional. No sustituye el tema existente hasta que el equipo lo aplique. */
private val AquaScheme = lightColorScheme(
    primary = AquaPalette.TealDark,
    onPrimary = AquaPalette.White,
    primaryContainer = AquaPalette.Mint,
    onPrimaryContainer = AquaPalette.Ocean,
    secondary = AquaPalette.OceanLight,
    onSecondary = AquaPalette.White,
    secondaryContainer = AquaPalette.Mint,
    tertiary = AquaPalette.Teal,
    background = AquaPalette.Ice,
    onBackground = AquaPalette.Ink,
    surface = AquaPalette.White,
    onSurface = AquaPalette.Ink,
    surfaceVariant = AquaPalette.Ice,
    onSurfaceVariant = AquaPalette.Muted,
    outline = AquaPalette.Border,
    error = AquaPalette.Alert
)

private val AquaShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp)
)

private val AquaTypography = Typography(
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 32.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 26.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 23.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontSize = 14.sp,
        lineHeight = 21.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        lineHeight = 19.sp
    )
)

@Composable
fun AquaFieldTheme(content: @Composable () -> Unit) {
    // Colores estables en la defensa, sin paleta dinámica del celular.
    MaterialTheme(
        colorScheme = AquaScheme,
        shapes = AquaShapes,
        typography = AquaTypography,
        content = content
    )
}
