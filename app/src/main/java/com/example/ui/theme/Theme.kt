package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

enum class CovenThemePalette {
    MYSTIC_MOON,
    DEEP_FOREST
}

data class CovenColors(
    val background: Color,
    val surface: Color,
    val surfaceCard: Color,
    val surfaceCardElevated: Color,
    val border: Color,
    val borderHighlight: Color,
    val primaryAccent: Color,
    val secondaryAccent: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val isDeepForest: Boolean
)

val LocalCovenColors = staticCompositionLocalOf {
    CovenColors(
        background = WitchyDeepNight,
        surface = WitchySurface,
        surfaceCard = WitchySurfaceCard,
        surfaceCardElevated = WitchySurfaceCardElevated,
        border = WitchyBorder,
        borderHighlight = WitchyBorderHighlight,
        primaryAccent = EnchantedGold,
        secondaryAccent = ManaLavender,
        textPrimary = TextPrimaryLight,
        textSecondary = TextSecondaryLight,
        textMuted = TextMuted,
        isDeepForest = false
    )
}

val MysticMoonColorScheme = darkColorScheme(
    primary = EnchantedGold,
    secondary = ManaLavender,
    tertiary = ElvenGreen,
    background = WitchyDeepNight,
    surface = WitchySurface,
    onPrimary = WitchyDeepNight,
    onSecondary = WitchyDeepNight,
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight
)

val DeepForestColorScheme = darkColorScheme(
    primary = ForestEmberGold,
    secondary = ForestMossGreen,
    tertiary = ForestFernGlow,
    background = ForestDeepNight,
    surface = ForestSurface,
    onPrimary = ForestDeepNight,
    onSecondary = ForestDeepNight,
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight
)

@Composable
fun MyApplicationTheme(
    palette: CovenThemePalette = CovenThemePalette.MYSTIC_MOON,
    content: @Composable () -> Unit
) {
    val colorScheme = if (palette == CovenThemePalette.DEEP_FOREST) DeepForestColorScheme else MysticMoonColorScheme
    val customColors = if (palette == CovenThemePalette.DEEP_FOREST) {
        CovenColors(
            background = ForestDeepNight,
            surface = ForestSurface,
            surfaceCard = ForestSurfaceCard,
            surfaceCardElevated = ForestSurfaceCardElevated,
            border = ForestBorder,
            borderHighlight = ForestBorderHighlight,
            primaryAccent = ForestEmberGold,
            secondaryAccent = ForestMossGreen,
            textPrimary = TextPrimaryLight,
            textSecondary = TextSecondaryLight,
            textMuted = TextMuted,
            isDeepForest = true
        )
    } else {
        CovenColors(
            background = WitchyDeepNight,
            surface = WitchySurface,
            surfaceCard = WitchySurfaceCard,
            surfaceCardElevated = WitchySurfaceCardElevated,
            border = WitchyBorder,
            borderHighlight = WitchyBorderHighlight,
            primaryAccent = EnchantedGold,
            secondaryAccent = ManaLavender,
            textPrimary = TextPrimaryLight,
            textSecondary = TextSecondaryLight,
            textMuted = TextMuted,
            isDeepForest = false
        )
    }

    CompositionLocalProvider(LocalCovenColors provides customColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
