package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// TRC Brand Color Schemes
// 60% Charcoal, 30% White/Sports Gray, 10% TRC Gold Accent
private val TRCDarkColorScheme = darkColorScheme(
    primary = TRCGold,
    onPrimary = TRCCharcoal,
    primaryContainer = TRCCharcoalSoft,
    onPrimaryContainer = TRCGold,
    secondary = TRCWhite,
    onSecondary = TRCCharcoal,
    secondaryContainer = TRCCharcoalBorder,
    onSecondaryContainer = TRCWhite,
    background = TRCCharcoal,
    surface = TRCCharcoalSoft,
    surfaceVariant = TRCCharcoalElevated,
    onBackground = TRCWhite,
    onSurface = TRCWhite,
    onSurfaceVariant = TRCGray,
    outline = TRCCharcoalBorder,
    outlineVariant = TRCCharcoalBorder,
    error = TRCRust,
    onError = TRCWhite,
    errorContainer = Color(0x33E4572E),
    onErrorContainer = Color(0xFFFFB4A1)
)

private val TRCLightColorScheme = lightColorScheme(
    primary = TRCCharcoal,
    onPrimary = TRCGold,
    primaryContainer = TRCGold,
    onPrimaryContainer = TRCCharcoal,
    secondary = TRCGold,
    onSecondary = TRCCharcoal,
    secondaryContainer = Color(0xFFFFF9DB),
    onSecondaryContainer = TRCCharcoal,
    background = Color(0xFFF4F4F5),
    surface = TRCWhite,
    surfaceVariant = Color(0xFFE4E4E7),
    onBackground = TRCCharcoal,
    onSurface = TRCCharcoal,
    onSurfaceVariant = TRCGrayDark,
    outline = Color(0xFFD4D4D8),
    outlineVariant = Color(0xFFE4E4E7),
    error = TRCRust,
    onError = TRCWhite,
    errorContainer = Color(0xFFFFDAD4),
    onErrorContainer = Color(0xFF410001)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to TRC's signature Charcoal & Gold identity
    dynamicColor: Boolean = false, // Keep TRC brand colors intact across devices
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> TRCDarkColorScheme
        else -> TRCDarkColorScheme // Use TRC dark identity as the canonical club experience
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
