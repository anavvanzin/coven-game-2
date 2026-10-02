package com.example.data.repository

import com.example.data.local.CovenDao
import com.example.data.model.NoteDocEntity
import com.example.data.model.TaskEntity
import kotlinx.coroutines.flow.Flow

class CovenRepository(private val dao: CovenDao) {
    val tasks: Flow<List<TaskEntity>> = dao.getAllTasks()
    val notes: Flow<List<NoteDocEntity>> = dao.getAllNotes()

    suspend fun addTask(task: TaskEntity): Long = dao.insertTask(task)
    suspend fun updateTask(task: TaskEntity) = dao.updateTask(task)
    suspend fun deleteTask(task: TaskEntity) = dao.deleteTask(task)
    suspend fun deleteTaskById(id: Long) = dao.deleteTaskById(id)

    suspend fun addNote(note: NoteDocEntity): Long = dao.insertNote(note)
    suspend fun updateNote(note: NoteDocEntity) = dao.updateNote(note)
    suspend fun deleteNote(note: NoteDocEntity) = dao.deleteNote(note)
    suspend fun deleteNoteById(id: Long) = dao.deleteNoteById(id)
}
