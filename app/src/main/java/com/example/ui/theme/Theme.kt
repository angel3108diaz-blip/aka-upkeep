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
    primary = MaintDarkPrimary,
    onPrimary = MaintDarkOnPrimary,
    primaryContainer = MaintDarkPrimaryContainer,
    onPrimaryContainer = MaintDarkOnPrimaryContainer,
    secondary = MaintDarkSecondary,
    onSecondary = MaintDarkOnSecondary,
    secondaryContainer = MaintDarkSecondaryContainer,
    onSecondaryContainer = MaintDarkOnSecondaryContainer,
    background = MaintDarkBackground,
    surface = MaintDarkSurface,
    surfaceVariant = MaintDarkSurfaceVariant
)

private val LightColorScheme = lightColorScheme(
    primary = MaintPrimary,
    onPrimary = MaintOnPrimary,
    primaryContainer = MaintPrimaryContainer,
    onPrimaryContainer = MaintOnPrimaryContainer,
    secondary = MaintSecondary,
    onSecondary = MaintOnSecondary,
    secondaryContainer = MaintSecondaryContainer,
    onSecondaryContainer = MaintOnSecondaryContainer,
    tertiary = MaintTertiary,
    onTertiary = MaintOnTertiary,
    tertiaryContainer = MaintTertiaryContainer,
    onTertiaryContainer = MaintOnTertiaryContainer,
    background = SlateBackground,
    surface = SlateSurface,
    surfaceVariant = SlateSurfaceVariant,
    onBackground = Color(0xFF1E293B),
    onSurface = Color(0xFF1E293B)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our crisp industrial theme as default
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
