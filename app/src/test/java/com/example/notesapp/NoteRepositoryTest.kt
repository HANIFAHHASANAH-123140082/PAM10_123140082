package com.example.notesapp

import com.example.notesapp.data.NoteRepository
import com.example.notesapp.db.Note
import com.example.notesapp.db.NotesDatabase
import com.example.notesapp.db.NotesQueries
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class NoteRepositoryTest {

    private val mockDatabase = mockk<NotesDatabase>()
    private val mockQueries = mockk<NotesQueries>()
    private lateinit var repository: NoteRepository

    // Data dummy untuk testing
    private val dummyNote = Note(
        id = 1L,
        title = "Test Note",
        content = "Test Content",
        created_at = 1000L,
        updated_at = 1000L
    )

    @Before
    fun setup() {
        every { mockDatabase.notesQueries } returns mockQueries
        repository = NoteRepository(mockDatabase)
    }

    // TEST 1: getAllNotes mengembalikan list notes
    @Test
    fun `getAllNotes returns list of notes`() = runTest {
        every { mockQueries.getAllNotes() } returns mockk {
            every { executeAsList() } returns listOf(dummyNote)
        }

        val result = repository.getAllNotes()

        assertEquals(1, result.size)
        assertEquals("Test Note", result[0].title)
    }

    // TEST 2: getAllNotes mengembalikan list kosong
    @Test
    fun `getAllNotes returns empty list when no notes`() = runTest {
        every { mockQueries.getAllNotes() } returns mockk {
            every { executeAsList() } returns emptyList()
        }

        val result = repository.getAllNotes()

        assertTrue(result.isEmpty())
    }

    // TEST 3: getNoteById mengembalikan note yang benar
    @Test
    fun `getNoteById returns correct note`() = runTest {
        every { mockQueries.getNoteById(1L) } returns mockk {
            every { executeAsOneOrNull() } returns dummyNote
        }

        val result = repository.getNoteById(1L)

        assertNotNull(result)
        assertEquals(1L, result?.id)
        assertEquals("Test Note", result?.title)
    }

    // TEST 4: getNoteById mengembalikan null jika tidak ada
    @Test
    fun `getNoteById returns null when note not found`() = runTest {
        every { mockQueries.getNoteById(99L) } returns mockk {
            every { executeAsOneOrNull() } returns null
        }

        val result = repository.getNoteById(99L)

        assertNull(result)
    }

    // TEST 5: deleteNote memanggil query delete
    @Test
    fun `deleteNote calls deleteNote query`() = runTest {
        every { mockQueries.deleteNote(1L) } just Runs

        repository.deleteNote(1L)

        verify { mockQueries.deleteNote(1L) }
    }
}