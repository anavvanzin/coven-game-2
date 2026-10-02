package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.NoteDocEntity
import com.example.data.model.TaskEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [TaskEntity::class, NoteDocEntity::class], version = 1, exportSchema = false)
abstract class CovenDatabase : RoomDatabase() {
    abstract fun covenDao(): CovenDao

    companion object {
        @Volatile
        private var INSTANCE: CovenDatabase? = null

        fun getInstance(context: Context): CovenDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CovenDatabase::class.java,
                    "coven_study_db"
                ).fallbackToDestructiveMigration(dropAllTables = true)
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            INSTANCE?.let { seedInitialData(it.covenDao()) }
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun seedInitialData(dao: CovenDao) {
            dao.insertTask(
                TaskEntity(
                    title = "Review Ancient Runes Chapter 4 (Calculus)",
                    category = "Study",
                    priority = "Urgent",
                    assignedTo = "Both",
                    xpReward = 80,
                    dueDate = "Today 5 PM",
                    studyNotes = "Ana: 'Got through problems 1-12, need to review chain rule!'"
                )
            )
            dao.insertTask(
                TaskEntity(
                    title = "Herbology: Mandrake Root Repotting Protocol",
                    category = "Spellcraft",
                    priority = "High",
                    assignedTo = "Ana",
                    xpReward = 65,
                    dueDate = "Tomorrow",
                    studyNotes = "Ana: 'Always wear earmuffs before handling mature roots!'"
                )
            )
            dao.insertTask(
                TaskEntity(
                    title = "Bake Hobbit Cinnamon Tea Scones for Study Sprint",
                    category = "Hobbit Feast",
                    priority = "Medium",
                    assignedTo = "Sabrina",
                    xpReward = 45,
                    dueDate = "Friday",
                    studyNotes = "Sabrina: 'Fresh clotted cream and strawberry jam ready!'"
                )
            )
            dao.insertNote(
                NoteDocEntity(
                    title = "✨ Welcome to Sabrina & Ana's Study Sanctum",
                    category = "Grimoire Notes",
                    content = "Welcome to our shared coven grimoire!\n\nHere we track our daily study quests, brew potions to generate randomized daily study buffs, draw ancient witch affirmations, and practice deep focus sessions together.\n\nBlessed be your exams and spells! ✨",
                    author = "Coven Duo ✨",
                    isPinned = true,
                    collaboratorTag = "Founding Grimoire Page"
                )
            )
        }
    }
}
