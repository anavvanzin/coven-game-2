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

    @Test
    fun testNoteMarkdownFormatting() {
        val note = com.example.data.model.NoteDocEntity(
            id = 1,
            title = "Herbology 101",
            category = "Potions",
            content = "Moonflowers bloom under waxing crescent.",
            author = "Ana 🧙‍♀️"
        )
        val md = com.example.util.CovenDownloadManager.formatNoteAsMarkdown(note)
        assertTrue(md.contains("# 📖 Herbology 101"))
        assertTrue(md.contains("Ana 🧙‍♀️"))
        assertTrue(md.contains("Moonflowers bloom"))
    }

    @Test
    fun testBackupJsonCreationAndParsing() {
        val tasks = listOf(
            com.example.data.model.TaskEntity(
                id = 1,
                title = "Study Runes",
                category = "Study",
                priority = "High",
                assignedTo = "Sabrina",
                isCompleted = false,
                xpReward = 50
            )
        )
        val notes = listOf(
            com.example.data.model.NoteDocEntity(
                id = 1,
                title = "Astral Runes",
                content = "Symbol of protection: Aegis",
                author = "Sabrina 🌙"
            )
        )

        val json = com.example.util.CovenDownloadManager.createBackupJson(
            tasks = tasks,
            notes = notes,
            xp = 600,
            level = 3,
            persona = com.example.data.model.CovenPersona.SABRINA
        )

        assertTrue(json.contains("Study Runes"))
        assertTrue(json.contains("Astral Runes"))

        val parseResult = com.example.util.CovenDownloadManager.parseBackupJson(json)
        assertTrue(parseResult.isSuccess)
        val backup = parseResult.getOrThrow()
        assertEquals(1, backup.tasks.size)
        assertEquals("Study Runes", backup.tasks[0].title)
        assertEquals(1, backup.notes.size)
        assertEquals("Astral Runes", backup.notes[0].title)
        assertEquals(600, backup.xp)
    }
}
