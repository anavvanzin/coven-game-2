package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class CovenPersona(val displayName: String, val avatarEmoji: String, val title: String, val tagColorHex: Long) {
    ANA("Ana 🧙‍♀️", "🧙‍♀️", "Potions & Herbology Prodigy", 0xFF6EE7B7),
    SABRINA("Sabrina 🌙", "🌙", "Astral Runes & Spellcraft Witch", 0xFFB588FF),
    DUO("Coven Duo ✨", "🔮", "Collaborative Study Sanctum", 0xFFF7C948)
}

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String = "Study", // Study, Spellcraft, Hobbit Feast, Exam Prep
    val priority: String = "High", // Urgent, High, Medium, Low
    val assignedTo: String = "Both", // Ana, Sabrina, Both
    val isCompleted: Boolean = false,
    val xpReward: Int = 50,
    val dueDate: String = "Today",
    val studyNotes: String = ""
)

@Entity(tableName = "notes")
data class NoteDocEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String = "Grimoire Notes",
    val content: String,
    val author: String = "Ana 🧙‍♀️",
    val isPinned: Boolean = false,
    val collaboratorTag: String = "Shared Sanctum Note",
    val timestamp: Long = System.currentTimeMillis()
)

data class MilestoneEntity(
    val id: Int,
    val title: String,
    val description: String,
    val emoji: String,
    val xpRequired: Int,
    val isUnlocked: Boolean = false
)
