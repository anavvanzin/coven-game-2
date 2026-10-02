package com.example.data.model

data class WitchyAffirmation(
    val id: Int,
    val quote: String,
    val themeTitle: String,
    val iconEmoji: String,
    val pixelIconName: String = "magic_rune",
    val authorOrOrigin: String,
    val auraColorHex: Long = 0xFFF7C948,
    val loreTip: String,
    val moonPhase: String = "🌙 Waxing Moon of Focus"
)

object WitchyAffirmationBank {
    val allAffirmations: List<WitchyAffirmation> = listOf(
        WitchyAffirmation(
            id = 1,
            quote = "Your magic is quiet, steady, and capable of moving ancient mountains. Brew your tea, breathe deep, and study on.",
            themeTitle = "Inner Stillness & Strength",
            iconEmoji = "🍵",
            authorOrOrigin = "Sabrina's Herbal Grimoire",
            auraColorHex = 0xFF4E9F5B,
            loreTip = "Peppermint & chamomile tea enhances focus and banishes mental fog.",
            moonPhase = "🌙 Waxing Crescent"
        ),
        WitchyAffirmation(
            id = 2,
            quote = "Even the greatest archmages took centuries to master their craft. One page, one spell, one equation at a time.",
            themeTitle = "Gentle Mastery",
            iconEmoji = "🧙‍♂️",
            authorOrOrigin = "Fellowship of the White Council",
            auraColorHex = 0xFFB588FF,
            loreTip = "Break insurmountable chapters into 15-minute bite-sized spell chunks.",
            moonPhase = "🌕 Full Moon of Illumination"
        ),
        WitchyAffirmation(
            id = 3,
            quote = "The cauldron of your mind turns every difficult concept into golden wisdom with enough patience and steady heat.",
            themeTitle = "Alchemy of Intellect",
            iconEmoji = "🧪",
            authorOrOrigin = "Potion Guild Proverb",
            auraColorHex = 0xFFF7C948,
            loreTip = "Active recall works like stirring potion ingredients clockwise to bind knowledge.",
            moonPhase = "🌖 Waning Gibbous"
        ),
        WitchyAffirmation(
            id = 4,
            quote = "Like starlight cutting through the deepest ancient forest canopy, your focus will illuminate every dark corner of this exam.",
            themeTitle = "Astral Clarity",
            iconEmoji = "🌙",
            authorOrOrigin = "Star Astrologer's Codex",
            auraColorHex = 0xFF70A6FF,
            loreTip = "Do the hardest problem first while your astral energy is peak.",
            moonPhase = "🌑 New Moon of New Beginnings"
        ),
        WitchyAffirmation(
            id = 5,
            quote = "A little magic, a trusted coven bestie, and crisp notes are stronger than any curse of procrastination.",
            themeTitle = "Fellowship Strength",
            iconEmoji = "🐈‍⬛",
            authorOrOrigin = "Sabrina & Ana's Study Pact",
            auraColorHex = 0xFFFF7B90,
            loreTip = "Study buddy accountability doubles mana retention and study completion speed.",
            moonPhase = "🌓 First Quarter Moon"
        ),
        WitchyAffirmation(
            id = 6,
            quote = "Your mind is an ancient, illuminated library filled with wonders waiting to be unlocked. Turn the page with courage.",
            themeTitle = "Scholar's Hearth",
            iconEmoji = "📖",
            authorOrOrigin = "Alexandria Tome of Scribes",
            auraColorHex = 0xFFD4A373,
            loreTip = "Writing margin summaries helps embed complex lore into long-term memory.",
            moonPhase = "🌔 Waxing Gibbous"
        ),
        WitchyAffirmation(
            id = 7,
            quote = "Plant the seeds of discipline today; harvest enchanted knowledge tomorrow under the glowing harvest moon.",
            themeTitle = "Earth Magic & Growth",
            iconEmoji = "🍄",
            authorOrOrigin = "Shire Herbalist Records",
            auraColorHex = 0xFF38D9A9,
            loreTip = "Consistency beats cramming every single time according to hobbit scholars.",
            moonPhase = "🌕 Harvest Moon"
        ),
        WitchyAffirmation(
            id = 8,
            quote = "No legendary potion was ever brewed in haste. Give your intellect the calm simmer and rest it deserves.",
            themeTitle = "Patient Craftsmanship",
            iconEmoji = "⚗️",
            authorOrOrigin = "Elven Apothecary Archives",
            auraColorHex = 0xFFB588FF,
            loreTip = "Sleep is when your brain weaves memory tapestries together. Rest without guilt!",
            moonPhase = "🌗 Third Quarter Moon"
        ),
        WitchyAffirmation(
            id = 9,
            quote = "Two witches working in sync can decipher the deepest mysteries of the multiverse. Keep sharing the light.",
            themeTitle = "Coven Harmony",
            iconEmoji = "✨",
            authorOrOrigin = "Sisterhood of Spells",
            auraColorHex = 0xFFF7C948,
            loreTip = "Teaching a concept to your study partner cements 90% of the material.",
            moonPhase = "🌙 Crescent Focus"
        ),
        WitchyAffirmation(
            id = 10,
            quote = "Breathe in starlight, exhale doubts. You possess the innate alchemy of relentless curiosity.",
            themeTitle = "Cosmic Breath",
            iconEmoji = "🌌",
            authorOrOrigin = "Celestial Oracle",
            auraColorHex = 0xFF845EC2,
            loreTip = "Take 3 deep slow breaths before opening your test paper to activate wizard calm.",
            moonPhase = "💫 Starlight Zenith"
        ),
        WitchyAffirmation(
            id = 11,
            quote = "The hardest runes reveal the greatest secrets of the realm. Keep transcribing, brilliant scholar.",
            themeTitle = "Runic Resilience",
            iconEmoji = "✍️",
            authorOrOrigin = "Dwarven Inscription Lore",
            auraColorHex = 0xFFFF9671,
            loreTip = "Difficult practice questions stimulate the strongest synaptic bridges.",
            moonPhase = "🌘 Waning Crescent"
        ),
        WitchyAffirmation(
            id = 12,
            quote = "Small daily rituals of focus compound into legendary sorcery and effortless mastery.",
            themeTitle = "Sacred Routine",
            iconEmoji = "🕯️",
            authorOrOrigin = "Candlelight Philosophy",
            auraColorHex = 0xFFFFC75F,
            loreTip = "Lighting a designated study candle signals your brain to enter deep flow.",
            moonPhase = "🌙 Waxing Crescent"
        ),
        WitchyAffirmation(
            id = 13,
            quote = "Ignite your study hearth. When doubts knock at your sanctum door, let your dedication answer with quiet grace.",
            themeTitle = "Sanctum Hearth",
            iconEmoji = "🔥",
            authorOrOrigin = "Ember Coven Chronicles",
            auraColorHex = 0xFFFF7B54,
            loreTip = "A warm beverage and cozy workspace lowers cortisol and sharpens retention.",
            moonPhase = "🔥 Solar Flare Moon"
        ),
        WitchyAffirmation(
            id = 14,
            quote = "Every exam question is merely a locked chest. You carry the skeleton key of practiced knowledge.",
            themeTitle = "The Unlocking Key",
            iconEmoji = "🗝️",
            authorOrOrigin = "Shadowcroft Academy",
            auraColorHex = 0xFFF7C948,
            loreTip = "Read questions thoroughly before writing to spot hidden clues.",
            moonPhase = "🗝️ Silver Key Crescent"
        ),
        WitchyAffirmation(
            id = 15,
            quote = "Let mistakes be like potion fumes that guide your recipe closer to perfection. You learn by brewing.",
            themeTitle = "Alchemical Grace",
            iconEmoji = "🍵",
            authorOrOrigin = "Ana's Cauldron Notes",
            auraColorHex = 0xFF70A6FF,
            loreTip = "Reviewing what you got wrong creates stronger neural pathways than re-reading right answers.",
            moonPhase = "🧪 Distillation Waxing"
        ),
        WitchyAffirmation(
            id = 16,
            quote = "You are weaving destiny with every stroke of your pen. Stand tall, student of the celestial arts.",
            themeTitle = "Weaver of Fates",
            iconEmoji = "✨",
            authorOrOrigin = "The Norns of Study Hall",
            auraColorHex = 0xFFB588FF,
            loreTip = "Celebrate small study wins to keep your dopamine and spirit soaring.",
            moonPhase = "🌟 Constellation Zenith"
        )
    )

    fun getRandomAffirmation(): WitchyAffirmation {
        return allAffirmations.random()
    }
}
