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

private val DarkColorScheme = darkColorScheme(
    primary = BentoSkyContainer,
    onPrimary = BentoNavyDark,
    primaryContainer = Indigo700,
    onPrimaryContainer = BentoSkyContainer,
    secondary = BentoLavenderContainer,
    onSecondary = BentoLavenderOnContainer,
    secondaryContainer = Color(0xFF381E72),
    onSecondaryContainer = BentoLavenderContainer,
    tertiary = BentoSageContainer,
    onTertiary = BentoSageOnContainer,
    tertiaryContainer = Color(0xFF234F28),
    onTertiaryContainer = BentoSageContainer,
    background = BackgroundDark,
    onBackground = OnSurfaceDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = OutlineDark,
    outlineVariant = Color(0xFF262B34),
    error = PriorityUrgent,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = BentoBluePrimary,
    onPrimary = Color.White,
    primaryContainer = BentoSkyContainer,
    onPrimaryContainer = BentoNavyDark,
    secondary = BentoLavenderOnContainer,
    onSecondary = Color.White,
    secondaryContainer = BentoLavenderContainer,
    onSecondaryContainer = BentoLavenderOnContainer,
    tertiary = BentoSageOnContainer,
    onTertiary = Color.White,
    tertiaryContainer = BentoSageContainer,
    onTertiaryContainer = BentoSageOnContainer,
    background = BentoBackgroundLight,
    onBackground = BentoTextPrimary,
    surface = SurfaceLight,
    onSurface = BentoTextPrimary,
    surfaceVariant = BentoNeutralCard,
    onSurfaceVariant = BentoTextSecondary,
    outline = BentoBorderBlue,
    outlineVariant = BentoBorderLight,
    error = PriorityUrgent,
    onError = Color.White
)

@Composable
fun SmartCampusTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // prefer intentional branded palette for consistency
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
