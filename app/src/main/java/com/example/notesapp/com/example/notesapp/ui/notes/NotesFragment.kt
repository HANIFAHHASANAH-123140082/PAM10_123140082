package com.example.notesapp.ui.notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notesapp.R
import com.example.notesapp.data.NoteRepository
import com.example.notesapp.data.createDatabase
import com.example.notesapp.databinding.FragmentNotesBinding

class NotesFragment : Fragment() {

    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: NotesViewModel
    private lateinit var adapter: NoteAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = createDatabase(requireContext())
        val repo = NoteRepository(db)
        viewModel = ViewModelProvider(this, NotesViewModelFactory(repo))[NotesViewModel::class.java]

        adapter = NoteAdapter(
            onClick = { note ->
                val action = NotesFragmentDirections.actionNotesToDetail(note.id)
                findNavController().navigate(action)
            },
            onDelete = { note -> viewModel.deleteNote(note.id) }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is NotesUiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                    binding.layoutEmpty.visibility = View.GONE
                }
                is NotesUiState.Empty -> {
                    binding.progressBar.visibility = View.GONE
                    binding.recyclerView.visibility = View.GONE
                    binding.layoutEmpty.visibility = View.VISIBLE
                }
                is NotesUiState.Content -> {
                    binding.progressBar.visibility = View.GONE
                    binding.layoutEmpty.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                    adapter.submitList(state.notes)
                    binding.tvNoteCount.text = "${state.notes.size} catatan tersimpan"
                }
                is NotesUiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                }
            }
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) viewModel.loadNotes()
                else viewModel.searchNotes(newText)
                return true
            }
        })

        binding.fabAdd.setOnClickListener {
            val action = NotesFragmentDirections.actionNotesToDetail(-1L)
            findNavController().navigate(action)
        }

        binding.btnSettings.setOnClickListener {
            findNavController().navigate(R.id.action_notes_to_settings)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}