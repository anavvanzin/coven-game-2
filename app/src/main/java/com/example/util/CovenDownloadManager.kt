package com.example.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.example.data.model.ActiveStudyBuff
import com.example.data.model.CovenPersona
import com.example.data.model.NoteDocEntity
import com.example.data.model.TaskEntity
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class DownloadResult(
    val isSuccess: Boolean,
    val filename: String,
    val message: String,
    val uri: Uri? = null
)

data class ParsedBackup(
    val tasks: List<TaskEntity>,
    val notes: List<NoteDocEntity>,
    val xp: Int = 0,
    val persona: String = "Ana 🧙‍♀️"
)

object CovenDownloadManager {

    private fun getCurrentDateFormatted(): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
    }

    private fun getFileTimestamp(): String {
        return SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    }

    /**
     * Downloads/saves text-based file to the device's public Downloads directory
     * and optionally opens the system share/save chooser.
     */
    fun downloadTextFile(
        context: Context,
        filename: String,
        content: String,
        mimeType: String = "text/plain",
        triggerShare: Boolean = true
    ): DownloadResult {
        return try {
            var savedUri: Uri? = null

            // 1. Save to MediaStore (Android 10+ / Q+) or External Storage
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/StudyCoven")
                }
                val resolver = context.contentResolver
                val contentUri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                if (contentUri != null) {
                    resolver.openOutputStream(contentUri)?.use { os ->
                        os.write(content.toByteArray(Charsets.UTF_8))
                    }
                    savedUri = contentUri
                }
            } else {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val covenDir = File(downloadsDir, "StudyCoven")
                if (!covenDir.exists()) covenDir.mkdirs()
                val targetFile = File(covenDir, filename)
                FileOutputStream(targetFile).use { fos ->
                    fos.write(content.toByteArray(Charsets.UTF_8))
                }
                savedUri = Uri.fromFile(targetFile)
            }

            // 2. Also save to app cache for reliable FileProvider sharing across devices/apps
            val cacheFile = File(context.cacheDir, filename)
            FileOutputStream(cacheFile).use { fos ->
                fos.write(content.toByteArray(Charsets.UTF_8))
            }

            val fileProviderUri = try {
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    cacheFile
                )
            } catch (e: Exception) {
                null
            }

            // 3. Trigger system chooser so user can open, copy to clipboard, or share
            if (triggerShare && fileProviderUri != null) {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = mimeType
                    putExtra(Intent.EXTRA_STREAM, fileProviderUri)
                    putExtra(Intent.EXTRA_SUBJECT, filename)
                    putExtra(Intent.EXTRA_TEXT, "Exported from Study Coven Sanctum: $filename")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                val chooser = Intent.createChooser(shareIntent, "Save or Open $filename").apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(chooser)
            }

            DownloadResult(
                isSuccess = true,
                filename = filename,
                message = "Downloaded '$filename' to Downloads/StudyCoven",
                uri = savedUri ?: fileProviderUri
            )
        } catch (e: Exception) {
            DownloadResult(
                isSuccess = false,
                filename = filename,
                message = "Download error: ${e.localizedMessage ?: "Unknown"}"
            )
        }
    }

    // Markdown Formatter for a Single Grimoire Note
    fun formatNoteAsMarkdown(note: NoteDocEntity): String {
        val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(note.timestamp))
        return buildString {
            appendLine("# 📖 ${note.title}")
            appendLine("> *Study Coven Sanctum Grimoire Parchment*")
            appendLine()
            appendLine("- **Author:** ${note.author}")
            appendLine("- **Category:** ${note.category}")
            appendLine("- **Collaborator Tag:** ${note.collaboratorTag}")
            appendLine("- **Timestamp:** $dateStr")
            appendLine("- **Pinned:** ${if (note.isPinned) "Yes ⭐" else "No"}")
            appendLine()
            appendLine("---")
            appendLine()
            appendLine(note.content)
            appendLine()
            appendLine("---")
            appendLine("*Exported from Study Coven • Bestie Study Companion* ✨")
        }
    }

    // Markdown Formatter for All Grimoire Notes
    fun formatAllNotesAsMarkdown(notes: List<NoteDocEntity>): String {
        return buildString {
            appendLine("# 📚 STUDY COVEN - COMPLETE GRIMOIRE ARCHIVE")
            appendLine("> *Exported on ${getCurrentDateFormatted()}*")
            appendLine()
            appendLine("Total Parchments: ${notes.size}")
            appendLine()
            appendLine("## Table of Spells & Notes")
            notes.forEachIndexed { index, note ->
                appendLine("${index + 1}. **${note.title}** (${note.category} by ${note.author})")
            }
            appendLine()
            appendLine("---")
            appendLine()
            notes.forEach { note ->
                appendLine(formatNoteAsMarkdown(note))
                appendLine()
                appendLine("---")
                appendLine()
            }
        }
    }

    // Text Formatter for Quests and To-Dos
    fun formatTasksAsText(tasks: List<TaskEntity>, level: Int, xp: Int): String {
        val completed = tasks.filter { it.isCompleted }
        val pending = tasks.filter { !it.isCompleted }

        return buildString {
            appendLine("==================================================")
            appendLine("🔮 STUDY COVEN: QUESTS & SYLLABI ARCHIVE")
            appendLine("Exported: ${getCurrentDateFormatted()}")
            appendLine("Coven Level: $level | Total Accrued XP: $xp")
            appendLine("==================================================")
            appendLine()
            appendLine("--- ACTIVE QUESTS (${pending.size}) ---")
            if (pending.isEmpty()) {
                appendLine("All quests conquered! Time for tea and potion brewing.")
            } else {
                pending.forEachIndexed { i, t ->
                    appendLine("[ ] ${i + 1}. ${t.title}")
                    appendLine("    • Category: ${t.category} | Priority: ${t.priority}")
                    appendLine("    • Assigned to: ${t.assignedTo} | Due: ${t.dueDate}")
                    appendLine("    • Reward: +${t.xpReward} XP")
                    if (t.studyNotes.isNotBlank()) {
                        appendLine("    • Notes: ${t.studyNotes}")
                    }
                    appendLine()
                }
            }
            appendLine()
            appendLine("--- COMPLETED QUESTS (${completed.size}) ---")
            if (completed.isEmpty()) {
                appendLine("No completed quests yet.")
            } else {
                completed.forEachIndexed { i, t ->
                    appendLine("[✓] ${i + 1}. ${t.title} (+${t.xpReward} XP)")
                    appendLine("    • Category: ${t.category} | Completed by: ${t.assignedTo}")
                }
            }
            appendLine()
            appendLine("==================================================")
            appendLine("Stay magical & keep studying! 🌙✨")
        }
    }

    // Comprehensive Study Report Formatter
    fun formatStudySummaryMarkdown(
        tasks: List<TaskEntity>,
        notes: List<NoteDocEntity>,
        xp: Int,
        level: Int,
        activeBuff: ActiveStudyBuff?,
        persona: CovenPersona
    ): String {
        val completedCount = tasks.count { it.isCompleted }
        val pendingCount = tasks.count { !it.isCompleted }
        val completionRate = if (tasks.isNotEmpty()) (completedCount * 100) / tasks.size else 0

        return buildString {
            appendLine("# 🔮 Study Coven - Comprehensive Study Session Report")
            appendLine("*Generated on ${getCurrentDateFormatted()}*")
            appendLine()
            appendLine("## 🧙‍♀️ Coven Status")
            appendLine("- **Active Persona:** ${persona.displayName} (${persona.title})")
            appendLine("- **Coven Mastery Level:** Level $level ($xp Total XP)")
            if (activeBuff != null) {
                appendLine("- **Active Cauldron Buff:** ${activeBuff.emoji} ${activeBuff.title} (*${activeBuff.description}*)")
            } else {
                appendLine("- **Active Cauldron Buff:** None currently brewed")
            }
            appendLine()
            appendLine("## 📊 Quest Progress Metrics")
            appendLine("- **Total Quests:** ${tasks.size}")
            appendLine("- **Completed Quests:** $completedCount")
            appendLine("- **Pending Quests:** $pendingCount")
            appendLine("- **Completion Rate:** $completionRate%")
            appendLine()
            appendLine("## 📖 Grimoire Inscriptions")
            appendLine("- **Total Inscribed Notes:** ${notes.size}")
            appendLine("- **Pinned Spells:** ${notes.count { it.isPinned }}")
            appendLine()
            appendLine("---")
            appendLine("*Generated and downloaded directly from Study Coven offline storage.*")
        }
    }

    // Full JSON Backup Exporter
    fun createBackupJson(
        tasks: List<TaskEntity>,
        notes: List<NoteDocEntity>,
        xp: Int,
        level: Int,
        persona: CovenPersona
    ): String {
        val root = JSONObject()
        root.put("app", "Study Coven")
        root.put("version", "1.0")
        root.put("exportedAt", getCurrentDateFormatted())
        root.put("totalXp", xp)
        root.put("level", level)
        root.put("persona", persona.name)

        val tasksArray = JSONArray()
        tasks.forEach { t ->
            val obj = JSONObject()
            obj.put("id", t.id)
            obj.put("title", t.title)
            obj.put("category", t.category)
            obj.put("priority", t.priority)
            obj.put("assignedTo", t.assignedTo)
            obj.put("isCompleted", t.isCompleted)
            obj.put("xpReward", t.xpReward)
            obj.put("dueDate", t.dueDate)
            obj.put("studyNotes", t.studyNotes)
            tasksArray.put(obj)
        }
        root.put("tasks", tasksArray)

        val notesArray = JSONArray()
        notes.forEach { n ->
            val obj = JSONObject()
            obj.put("id", n.id)
            obj.put("title", n.title)
            obj.put("category", n.category)
            obj.put("content", n.content)
            obj.put("author", n.author)
            obj.put("isPinned", n.isPinned)
            obj.put("collaboratorTag", n.collaboratorTag)
            obj.put("timestamp", n.timestamp)
            notesArray.put(obj)
        }
        root.put("notes", notesArray)

        return root.toString(2)
    }

    // JSON Backup Parser
    fun parseBackupJson(jsonString: String): Result<ParsedBackup> {
        return try {
            val root = JSONObject(jsonString)
            val xp = root.optInt("totalXp", 0)
            val persona = root.optString("persona", "DUO")

            val tasksList = mutableListOf<TaskEntity>()
            val tasksArray = root.optJSONArray("tasks")
            if (tasksArray != null) {
                for (i in 0 until tasksArray.length()) {
                    val obj = tasksArray.getJSONObject(i)
                    tasksList.add(
                        TaskEntity(
                            id = 0, // Generate new IDs upon restore to prevent collisions
                            title = obj.optString("title", "Imported Quest"),
                            category = obj.optString("category", "Study"),
                            priority = obj.optString("priority", "High"),
                            assignedTo = obj.optString("assignedTo", "Both"),
                            isCompleted = obj.optBoolean("isCompleted", false),
                            xpReward = obj.optInt("xpReward", 50),
                            dueDate = obj.optString("dueDate", "Today"),
                            studyNotes = obj.optString("studyNotes", "")
                        )
                    )
                }
            }

            val notesList = mutableListOf<NoteDocEntity>()
            val notesArray = root.optJSONArray("notes")
            if (notesArray != null) {
                for (i in 0 until notesArray.length()) {
                    val obj = notesArray.getJSONObject(i)
                    notesList.add(
                        NoteDocEntity(
                            id = 0, // Generate new IDs upon restore
                            title = obj.optString("title", "Imported Note"),
                            category = obj.optString("category", "Grimoire Notes"),
                            content = obj.optString("content", ""),
                            author = obj.optString("author", "Ana 🧙‍♀️"),
                            isPinned = obj.optBoolean("isPinned", false),
                            collaboratorTag = obj.optString("collaboratorTag", "Restored from Backup"),
                            timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                        )
                    )
                }
            }

            Result.success(ParsedBackup(tasksList, notesList, xp, persona))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
