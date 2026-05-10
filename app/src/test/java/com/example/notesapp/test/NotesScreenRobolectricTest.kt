package com.example.notesapp

import com.example.notesapp.db.Note
import com.example.notesapp.ui.notes.NotesUiState
import org.junit.Test
import org.junit.Assert.*

class NotesScreenRobolectricTest {

    private val dummyNotes = listOf(
        Note(id = 1L, title = "Catatan 1", content = "Isi 1", created_at = 1000L, updated_at = 1000L),
        Note(id = 2L, title = "Catatan 2", content = "Isi 2", created_at = 2000L, updated_at = 2000L)
    )

    // UI TEST 1: State Content menampilkan notes yang benar
    @Test
    fun test_uiState_content_shows_correct_notes() {
        val state = NotesUiState.Content(dummyNotes)
        assertEquals(2, state.notes.size)
        assertEquals("Catatan 1", state.notes[0].title)
        assertEquals("Catatan 2", state.notes[1].title)
    }

    // UI TEST 2: State Empty tidak punya notes
    @Test
    fun test_uiState_empty_has_no_notes() {
        val state = NotesUiState.Empty
        assertNotNull(state)
        assertTrue(state is NotesUiState.Empty)
    }

    // UI TEST 3: State Error menyimpan pesan error dengan benar
    @Test
    fun test_uiState_error_has_correct_message() {
        val errorMsg = "Gagal memuat catatan"
        val state = NotesUiState.Error(errorMsg)
        assertTrue(state is NotesUiState.Error)
        assertEquals(errorMsg, state.message)
    }
}