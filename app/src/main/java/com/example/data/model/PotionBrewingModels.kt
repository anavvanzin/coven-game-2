package com.example.data.model

data class PotionIngredient(
    val id: String,
    val name: String,
    val emoji: String,
    val description: String,
    val rarityColorHex: Long = 0xFF6EE7B7
)

data class ActiveStudyBuff(
    val title: String,
    val description: String,
    val emoji: String,
    val effectDuration: String = "All Day (Study Sprint)",
    val xpBonusPercent: Int = 25
)

sealed class BrewingState {
    object Idle : BrewingState()
    object Brewing : BrewingState()
    data class Success(val buff: ActiveStudyBuff) : BrewingState()
    data class Error(val message: String) : BrewingState()
}

object PotionGrimoire {
    val ALL_INGREDIENTS = listOf(
        PotionIngredient("mandrake", "Mandrake Root", "🌱", "Earthy focus stabilizer", 0xFF6EE7B7),
        PotionIngredient("moonstone", "Moonstone Dust", "✨", "Celestial memory crystal", 0xFF70A6FF),
        PotionIngredient("dragon_scale", "Dragon Scale", "🐉", "Endurance and flame grit", 0xFFFF7B90),
        PotionIngredient("nightshade", "Ghost Lily Petal", "🌸", "Intuition and pattern spotting", 0xFFB588FF),
        PotionIngredient("starlight_dew", "Starlight Dew", "💧", "Clarity against mental fog", 0xFF38BDF8),
        PotionIngredient("phoenix_feather", "Phoenix Ash", "🪶", "Rebirth from exam burnout", 0xFFF59E0B)
    )

    val RANDOM_BUFFS = listOf(
        ActiveStudyBuff(
            title = "Hyper-Focus Surge",
            description = "Blocks all distracting thoughts; mana doubles during 25-minute sprints!",
            emoji = "⚡",
            xpBonusPercent = 30
        ),
        ActiveStudyBuff(
            title = "Photographic Memory Mist",
            description = "Formulas and vocabulary stick effortlessly like enchanted ink on parchment.",
            emoji = "📖",
            xpBonusPercent = 25
        ),
        ActiveStudyBuff(
            title = "Unshakable Serenity",
            description = "Banishes test anxiety and replaces it with calm, steady archmage confidence.",
            emoji = "🍵",
            xpBonusPercent = 20
        ),
        ActiveStudyBuff(
            title = "Astral Speed Spell",
            description = "Reading speed and comprehension increase by 40% for tonight's review.",
            emoji = "✨",
            xpBonusPercent = 35
        ),
        ActiveStudyBuff(
            title = "Coven Synergy Ward",
            description = "Shared study quests award bonus XP when studying alongside your bestie.",
            emoji = "🔮",
            xpBonusPercent = 50
        )
    )
}
