package com.example.notesapp.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.notesapp.data.NoteRepository
import com.example.notesapp.data.createDatabase
import com.example.notesapp.databinding.FragmentNoteDetailBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class NoteDetailFragment : Fragment() {

    private var _binding: FragmentNoteDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var repository: NoteRepository
    private var noteId: Long = -1L  // ← satu saja, tidak pakai navArgs

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoteDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = createDatabase(requireContext())
        repository = NoteRepository(db)

        // ← Ganti args.noteId jadi Bundle
        noteId = arguments?.getLong("noteId", -1L) ?: -1L

        if (noteId != -1L) {
            binding.tvToolbarTitle.text = "Edit Catatan"
            binding.btnDelete.visibility = View.VISIBLE
            loadNote()
        } else {
            binding.tvToolbarTitle.text = "Catatan Baru"
            binding.btnDelete.visibility = View.GONE
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnSave.setOnClickListener {
            val title = binding.etTitle.text.toString().trim()
            val content = binding.etContent.text.toString().trim()

            if (title.isEmpty()) {
                binding.etTitle.error = "Judul tidak boleh kosong"
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.Main).launch {
                if (noteId == -1L) {
                    repository.insertNote(title, content)
                    Toast.makeText(context, "Catatan disimpan!", Toast.LENGTH_SHORT).show()
                } else {
                    repository.updateNote(noteId, title, content)
                    Toast.makeText(context, "Catatan diupdate!", Toast.LENGTH_SHORT).show()
                }
                findNavController().popBackStack()
            }
        }

        binding.btnDelete.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                repository.deleteNote(noteId)
                Toast.makeText(context, "Catatan dihapus!", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }
    }

    private fun loadNote() {
        CoroutineScope(Dispatchers.Main).launch {
            val note = withContext(Dispatchers.IO) { repository.getNoteById(noteId) }
            note?.let {
                binding.etTitle.setText(it.title)
                binding.etContent.setText(it.content)
                val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
                binding.tvCreatedAt.text = "Dibuat: ${sdf.format(Date(it.created_at))}"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}