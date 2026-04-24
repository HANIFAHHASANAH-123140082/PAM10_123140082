package com.example.notesapp.data

import com.example.notesapp.db.NotesDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NoteRepository(private val database: NotesDatabase) {

    private val queries = database.notesQueries

    suspend fun getAllNotes() = withContext(Dispatchers.IO) {
        queries.getAllNotes().executeAsList()
    }

    suspend fun searchNotes(query: String) = withContext(Dispatchers.IO) {
        queries.searchNotes(query).executeAsList()
    }

    suspend fun getNoteById(id: Long) = withContext(Dispatchers.IO) {
        queries.getNoteById(id).executeAsOneOrNull()
    }

    suspend fun insertNote(title: String, content: String) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        queries.insertNote(title, content, now, now)
    }

    suspend fun updateNote(id: Long, title: String, content: String) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        queries.updateNote(title, content, now, id)
    }

    suspend fun deleteNote(id: Long) = withContext(Dispatchers.IO) {
        queries.deleteNote(id)
    }
}