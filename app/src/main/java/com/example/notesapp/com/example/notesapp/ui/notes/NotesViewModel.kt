package com.example.notesapp.ui.notes

import androidx.lifecycle.*
import com.example.notesapp.data.NoteRepository
import com.example.notesapp.db.Note
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class NotesUiState {
    object Loading : NotesUiState()
    object Empty : NotesUiState()
    data class Content(val notes: List<Note>) : NotesUiState()
    data class Error(val message: String) : NotesUiState()
}

class NotesViewModel(private val repository: NoteRepository) : ViewModel() {

    private val _uiState = MutableLiveData<NotesUiState>(NotesUiState.Loading)
    val uiState: LiveData<NotesUiState> = _uiState

    private val _uiStateFlow = MutableStateFlow<NotesUiState>(NotesUiState.Loading)
    val uiStateFlow: StateFlow<NotesUiState> = _uiStateFlow.asStateFlow()

    init { loadNotes() }

    fun loadNotes() {
        _uiState.value = NotesUiState.Loading
        _uiStateFlow.value = NotesUiState.Loading
        viewModelScope.launch {
            try {
                val notes = repository.getAllNotes()
                val state = if (notes.isEmpty()) NotesUiState.Empty
                else NotesUiState.Content(notes)
                _uiState.value = state
                _uiStateFlow.value = state
            } catch (e: Exception) {
                val err = NotesUiState.Error(e.message ?: "Error")
                _uiState.value = err
                _uiStateFlow.value = err
            }
        }
    }

    fun searchNotes(query: String) {
        viewModelScope.launch {
            val notes = repository.searchNotes(query)
            val state = if (notes.isEmpty()) NotesUiState.Empty
            else NotesUiState.Content(notes)
            _uiState.value = state
            _uiStateFlow.value = state
        }
    }

    fun deleteNote(id: Long) {
        viewModelScope.launch {
            repository.deleteNote(id)
            loadNotes()
        }
    }
}

class NotesViewModelFactory(private val repository: NoteRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return NotesViewModel(repository) as T
    }
}