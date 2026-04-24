package com.example.notesapp.ui.notes

import androidx.lifecycle.*
import com.example.notesapp.data.NoteRepository
import com.example.notesapp.db.Note
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

    init { loadNotes() }

    fun loadNotes() {
        _uiState.value = NotesUiState.Loading
        viewModelScope.launch {
            try {
                val notes = repository.getAllNotes()
                _uiState.value = if (notes.isEmpty()) NotesUiState.Empty
                else NotesUiState.Content(notes)
            } catch (e: Exception) {
                _uiState.value = NotesUiState.Error(e.message ?: "Error")
            }
        }
    }

    fun searchNotes(query: String) {
        viewModelScope.launch {
            val notes = repository.searchNotes(query)
            _uiState.value = if (notes.isEmpty()) NotesUiState.Empty
            else NotesUiState.Content(notes)
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