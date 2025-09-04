package com.example.orbisai.ui.theme

import android.os.Build
import androidx.compose.animation.core.*
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- Paletas fallback mejoradas para Material 3
private val LightColorSchemeFallback = lightColorScheme(
    primary = Color(0xFF1565C0),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDCEFFF),
    onPrimaryContainer = Color(0xFF001E36),
    secondary = Color(0xFF2E7D32),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE8F5E8),
    onSecondaryContainer = Color(0xFF002105),
    tertiary = Color(0xFF7B1FA2),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFF3D8FF),
    onTertiaryContainer = Color(0xFF2A0054),
    background = Color(0xFFF6F8FB),
    onBackground = Color(0xFF191C20),
    surface = Color.White,
    onSurface = Color(0xFF191C20),
    surfaceVariant = Color(0xFFF1F3F4),
    onSurfaceVariant = Color(0xFF42474E),
    outline = Color(0xFF73777F),
    outlineVariant = Color(0xFFC3C7CF),
    error = Color(0xFFD32F2F),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    // Colores adicionales para Material 3
    surfaceTint = Color(0xFF1565C0),
    inverseSurface = Color(0xFF2F3033),
    inverseOnSurface = Color(0xFFF1F1F4),
    inversePrimary = Color(0xFF90CAF9),
    scrim = Color(0xFF000000)
)

private val DarkColorSchemeFallback = darkColorScheme(
    primary = Color(0xFF90CAF9),
    onPrimary = Color(0xFF003258),
    primaryContainer = Color(0xFF004881),
    onPrimaryContainer = Color(0xFFDCEFFF),
    secondary = Color(0xFF81C784),
    onSecondary = Color(0xFF003910),
    secondaryContainer = Color(0xFF005318),
    onSecondaryContainer = Color(0xFFE8F5E8),
    tertiary = Color(0xFFE1B5FF),
    onTertiary = Color(0xFF420075),
    tertiaryContainer = Color(0xFF5B009C),
    onTertiaryContainer = Color(0xFFF3D8FF),
    background = Color(0xFF0F1720),
    onBackground = Color(0xFFE1E2E4),
    surface = Color(0xFF111827),
    onSurface = Color(0xFFE1E2E4),
    surfaceVariant = Color(0xFF42474E),
    onSurfaceVariant = Color(0xFFC3C7CF),
    outline = Color(0xFF8D9199),
    outlineVariant = Color(0xFF42474E),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    // Colores adicionales para Material 3
    surfaceTint = Color(0xFF90CAF9),
    inverseSurface = Color(0xFFE1E2E4),
    inverseOnSurface = Color(0xFF191C20),
    inversePrimary = Color(0xFF1565C0),
    scrim = Color(0xFF000000)
)

// --- Typography mejorada para Material 3
private val AppTypography = Typography(
    displayLarge = Typography().displayLarge.copy(
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
    ),
    displayMedium = Typography().displayMedium.copy(
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp,
        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
    ),
    displaySmall = Typography().displaySmall.copy(
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
    ),
    headlineLarge = Typography().headlineLarge.copy(
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
    ),
    headlineMedium = Typography().headlineMedium.copy(
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp,
        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
    ),
    headlineSmall = Typography().headlineSmall.copy(
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
    ),
    titleLarge = Typography().titleLarge.copy(
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
    ),
    titleMedium = Typography().titleMedium.copy(
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
    ),
    titleSmall = Typography().titleSmall.copy(
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
    ),
    bodyLarge = Typography().bodyLarge.copy(
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Normal
    ),
    bodyMedium = Typography().bodyMedium.copy(
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Normal
    ),
    bodySmall = Typography().bodySmall.copy(
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Normal
    ),
    labelLarge = Typography().labelLarge.copy(
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
    ),
    labelMedium = Typography().labelMedium.copy(
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
    ),
    labelSmall = Typography().labelSmall.copy(
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
    )
)

// --- Shapes mejoradas para Material 3
private val AppShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraSmall = RoundedCornerShape(4.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

// --- Animaciones para Material 3
object OrbisAnimations {
    val enterTransition = tween<Float>(
        durationMillis = 300,
        easing = FastOutSlowInEasing
    )
    
    val exitTransition = tween<Float>(
        durationMillis = 250,
        easing = FastOutLinearInEasing
    )
    
    val contentAlpha = tween<Float>(
        durationMillis = 200,
        easing = LinearEasing
    )
    
    val scaleIn = tween<Float>(
        durationMillis = 300,
        easing = FastOutSlowInEasing
    )
    
    val slideIn = tween<Float>(
        durationMillis = 300,
        easing = FastOutSlowInEasing
    )
}

/**
 * OrbisAITheme - Material 3 con Dynamic Color
 * Incluye mejoras para animaciones, estados y transiciones fluidas
 *
 * @param dynamicColor enable/disable dynamic color (default true)
 * @param useDarkTheme set dark mode manually (default system)
 * @param content composable content
 */
@Composable
fun OrbisAITheme(
    dynamicColor: Boolean = true,
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (useDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        useDarkTheme -> DarkColorSchemeFallback
        else -> LightColorSchemeFallback
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}