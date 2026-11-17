package com.lovepaws.mobile.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LovePawsLightColorScheme = lightColorScheme(
    primary = LovePawsPrimary,
    onPrimary = LovePawsSurface,
    primaryContainer = LovePawsPrimaryContainer,
    onPrimaryContainer = LovePawsPrimaryDark,

    secondary = LovePawsSecondary,
    onSecondary = LovePawsSurface,
    secondaryContainer = LovePawsSecondaryContainer,
    onSecondaryContainer = LovePawsSecondaryDark,

    tertiary = LovePawsTertiary,
    onTertiary = LovePawsSurface,
    tertiaryContainer = LovePawsTertiaryContainer,
    onTertiaryContainer = LovePawsTertiaryDark,

    error = LovePawsError,
    onError = LovePawsSurface,

    background = LovePawsBackground,
    onBackground = LovePawsOnBackground,

    surface = LovePawsSurface,
    onSurface = LovePawsOnSurface,
    surfaceVariant = LovePawsSurfaceVariant,
    onSurfaceVariant = LovePawsOnSurfaceVariant,

    outline = LovePawsOutline,
    outlineVariant = LovePawsOutlineVariant
)

private val LovePawsDarkColorScheme = darkColorScheme(
    primary = LovePawsPrimaryContainer,
    onPrimary = LovePawsPrimaryDark,
    primaryContainer = LovePawsPrimary,
    onPrimaryContainer = LovePawsPrimaryContainer,

    secondary = Color(0xFFB4C9B2),
    onSecondary = LovePawsSecondaryDark,
    secondaryContainer = Color(0xFF3B513D),
    onSecondaryContainer = LovePawsSecondaryContainer,

    tertiary = Color(0xFFE9C28D),
    onTertiary = LovePawsTertiaryDark,
    tertiaryContainer = Color(0xFF63451D),
    onTertiaryContainer = LovePawsTertiaryContainer,

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),

    background = LovePawsDarkBackground,
    onBackground = LovePawsDarkOnSurface,

    surface = LovePawsDarkSurface,
    onSurface = LovePawsDarkOnSurface,
    surfaceVariant = LovePawsDarkSurfaceVariant,
    onSurfaceVariant = LovePawsDarkOnSurfaceVariant,

    outline = LovePawsDarkOutline,
    outlineVariant = LovePawsDarkOutlineVariant
)

private val LovePawsShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp)
)

@Composable
fun LovePawsMobileTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        LovePawsDarkColorScheme
    } else {
        LovePawsLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = LovePawsShapes,
        content = content
    )
}