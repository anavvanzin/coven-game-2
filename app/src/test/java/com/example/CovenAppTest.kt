package com.example

import com.example.data.model.PotionGrimoire
import com.example.data.model.WitchyAffirmationBank
import org.junit.Assert.*
import org.junit.Test

class CovenAppTest {

    @Test
    fun testWitchyAffirmationBankNotEmpty() {
        assertTrue(WitchyAffirmationBank.allAffirmations.isNotEmpty())
        val affirmation = WitchyAffirmationBank.getRandomAffirmation()
        assertNotNull(affirmation)
        assertTrue(affirmation.quote.isNotBlank())
        assertTrue(affirmation.loreTip.isNotBlank())
    }

    @Test
    fun testPotionIngredientsAndBuffs() {
        assertTrue(PotionGrimoire.ALL_INGREDIENTS.size >= 4)
        assertTrue(PotionGrimoire.RANDOM_BUFFS.isNotEmpty())
        val buff = PotionGrimoire.RANDOM_BUFFS.random()
        assertNotNull(buff)
        assertTrue(buff.title.isNotBlank())
        assertTrue(buff.xpBonusPercent > 0)
    }

    @Test
    fun testCovenPersonas() {
        val personas = com.example.data.model.CovenPersona.values()
        assertEquals(3, personas.size)
        assertTrue(personas.any { it.name == "ANA" })
        assertTrue(personas.any { it.name == "SABRINA" })
        assertTrue(personas.any { it.name == "DUO" })
    }

    @Test
    fun testSoundscapeTypes() {
        val soundscapes = com.example.data.model.SoundscapeType.values()
        assertTrue(soundscapes.size >= 3)
        assertTrue(soundscapes.any { it.id == "rainy_forest" })
        assertTrue(soundscapes.any { it.id == "crackling_fireplace" })
        assertTrue(soundscapes.any { it.id == "ancient_library" })

        soundscapes.forEach { s ->
            assertTrue(s.title.isNotBlank())
            assertTrue(s.subtitle.isNotBlank())
            assertTrue(s.iconEmoji.isNotBlank())
            assertTrue(s.loreDescription.isNotBlank())
        }
    }
}
