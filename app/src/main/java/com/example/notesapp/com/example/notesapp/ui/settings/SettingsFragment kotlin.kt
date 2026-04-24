package com.example.notesapp.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.notesapp.datastore.SettingsDataStore
import com.example.notesapp.databinding.FragmentSettingsBinding
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var settingsDataStore: SettingsDataStore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settingsDataStore = SettingsDataStore(requireContext())

        lifecycleScope.launch {
            settingsDataStore.theme.collect { theme ->
                if (theme == "dark") binding.rbDark.isChecked = true
                else binding.rbLight.isChecked = true
            }
        }

        lifecycleScope.launch {
            settingsDataStore.sortOrder.collect { sort ->
                if (sort == "newest") binding.rbNewest.isChecked = true
                else binding.rbOldest.isChecked = true
            }
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnSaveSettings.setOnClickListener {
            lifecycleScope.launch {
                val theme = if (binding.rbDark.isChecked) "dark" else "light"
                val sort = if (binding.rbNewest.isChecked) "newest" else "oldest"
                settingsDataStore.setTheme(theme)
                settingsDataStore.setSortOrder(sort)
                findNavController().popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}