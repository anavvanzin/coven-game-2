package com.example.data.model

enum class SoundscapeType(
    val id: String,
    val title: String,
    val subtitle: String,
    val iconEmoji: String,
    val colorHex: Long,
    val loreDescription: String
) {
    RAINY_FOREST(
        id = "rainy_forest",
        title = "Rainy Forest",
        subtitle = "Pine Canopy & Gentle Showers",
        iconEmoji = "🌲🌧️",
        colorHex = 0xFF6EE7B7,
        loreDescription = "Soft rainfall whispering through ancient mallorn pines, carrying distant thunder and petrichor to soothe study fatigue."
    ),
    CRACKLING_FIREPLACE(
        id = "crackling_fireplace",
        title = "Crackling Fireplace",
        subtitle = "Warm Hearth & Oak Embers",
        iconEmoji = "🔥🪵",
        colorHex = 0xFFF97316,
        loreDescription = "A cozy stone hearth in the coven sanctum with snap-crackling cedar logs, glowing coals, and soothing ambient warmth."
    ),
    ANCIENT_LIBRARY(
        id = "ancient_library",
        title = "Ancient Library",
        subtitle = "Gothic Arches & Soft Pendulum",
        iconEmoji = "📚🕯️",
        colorHex = 0xFFB588FF,
        loreDescription = "Vast arched sanctum filled with centuries of grimoires, gentle velvet room presence, and a distant mahogany clock ticking."
    ),
    MIDNIGHT_SANCTUM(
        id = "midnight_sanctum",
        title = "Midnight Sanctum",
        subtitle = "Binaural Theta & Wind Chimes",
        iconEmoji = "🌙✨",
        colorHex = 0xFF60A5FA,
        loreDescription = "Enchanted night breeze carrying distant glass wind chimes and calming low-frequency harmonic hum for deep focus."
    ),
    BUBBLING_CAULDRON(
        id = "bubbling_cauldron",
        title = "Potion Laboratory",
        subtitle = "Herbal Simmer & Glass Vials",
        iconEmoji = "🧪🫧",
        colorHex = 0xFF34D399,
        loreDescription = "Gentle simmering cauldron infused with peppermint and lavender, soft potion bubble pops, and soothing alchemical white noise."
    )
}

data class SoundscapePlaybackState(
    val currentType: SoundscapeType = SoundscapeType.RAINY_FOREST,
    val isPlaying: Boolean = false,
    val volume: Float = 0.75f,
    val timerRemainingSeconds: Int = 0,
    val selectedTimerPreset: Int = 0 // 0 means Infinite, or 15, 25, 45, 60 minutes
)
