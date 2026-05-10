package com.example.notesapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.notesapp.data.NoteRepository
import com.example.notesapp.db.Note
import com.example.notesapp.ui.notes.NotesUiState
import com.example.notesapp.ui.notes.NotesViewModel
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class NotesViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private val mockRepository = mockk<NoteRepository>()
    private lateinit var viewModel: NotesViewModel

    private val dummyNote = Note(
        id = 1L,
        title = "Test Note",
        content = "Isi catatan",
        created_at = 1000L,
        updated_at = 1000L
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    // TEST 1: loadNotes berhasil → uiState jadi Content
    @Test
    fun `loadNotes success sets uiState to Content`() = runTest {
        coEvery { mockRepository.getAllNotes() } returns listOf(dummyNote)

        viewModel = NotesViewModel(mockRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is NotesUiState.Content)
        assertEquals(1, (state as NotesUiState.Content).notes.size)
    }

    // TEST 2: loadNotes kosong → uiState jadi Empty
    @Test
    fun `loadNotes empty sets uiState to Empty`() = runTest {
        coEvery { mockRepository.getAllNotes() } returns emptyList()

        viewModel = NotesViewModel(mockRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value is NotesUiState.Empty)
    }

    // TEST 3: loadNotes error → uiState jadi Error
    @Test
    fun `loadNotes error sets uiState to Error`() = runTest {
        coEvery { mockRepository.getAllNotes() } throws Exception("DB error")

        viewModel = NotesViewModel(mockRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is NotesUiState.Error)
        assertEquals("DB error", (state as NotesUiState.Error).message)
    }

    // TEST 4: deleteNote memanggil repository.deleteNote
    @Test
    fun `deleteNote calls repository deleteNote`() = runTest {
        coEvery { mockRepository.getAllNotes() } returns listOf(dummyNote)
        coEvery { mockRepository.deleteNote(any()) } just Runs

        viewModel = NotesViewModel(mockRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.deleteNote(1L)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { mockRepository.deleteNote(1L) }
    }

    // FLOW TEST 1 (Turbine): uiStateFlow emit Loading dulu lalu Content
    @Test
    fun `uiStateFlow emits Loading then Content`() = runTest {
        coEvery { mockRepository.getAllNotes() } returns listOf(dummyNote)

        viewModel = NotesViewModel(mockRepository)

        viewModel.uiStateFlow.test {
            val first = awaitItem()
            assertTrue(first is NotesUiState.Loading)

            testDispatcher.scheduler.advanceUntilIdle()

            val second = awaitItem()
            assertTrue(second is NotesUiState.Content)

            cancelAndIgnoreRemainingEvents()
        }
    }

    // FLOW TEST 2 (Turbine): uiStateFlow emit Empty ketika notes kosong
    @Test
    fun `uiStateFlow emits Empty when no notes`() = runTest {
        coEvery { mockRepository.getAllNotes() } returns emptyList()

        viewModel = NotesViewModel(mockRepository)

        viewModel.uiStateFlow.test {
            awaitItem() // Loading

            testDispatcher.scheduler.advanceUntilIdle()

            val state = awaitItem()
            assertTrue(state is NotesUiState.Empty)

            cancelAndIgnoreRemainingEvents()
        }
    }
}