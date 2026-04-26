package com.example.notesapp.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.notesapp.datastore.SettingsDataStore
import com.example.notesapp.databinding.FragmentSettingsBinding
import com.example.notesapp.platform.DeviceInfo
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var settingsDataStore: SettingsDataStore
    private val deviceInfo: DeviceInfo by inject()

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

        // Tampilkan Device Info
        binding.tvDeviceName.text = "📱 Device: ${deviceInfo.getDeviceName()}"
        binding.tvOsVersion.text = "🤖 OS: ${deviceInfo.getOsVersion()}"
        binding.tvAppVersion.text = "📦 App Version: ${deviceInfo.getAppVersion()}"

        // Load tema tersimpan
        lifecycleScope.launch {
            settingsDataStore.theme.collect { theme ->
                if (theme == "dark") binding.rbDark.isChecked = true
                else binding.rbLight.isChecked = true
            }
        }

        // Load sort order tersimpan
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

                // Simpan ke DataStore
                settingsDataStore.setTheme(theme)
                settingsDataStore.setSortOrder(sort)

                // ✅ Apply tema langsung tanpa restart app
                when (theme) {
                    "dark" -> AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_YES
                    )
                    "light" -> AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_NO
                    )
                }

                findNavController().popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}