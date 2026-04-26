package com.example.notesapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.example.notesapp.databinding.ActivityMainBinding
import com.example.notesapp.datastore.SettingsDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        // ✅ Apply tema SEBELUM super.onCreate agar tidak flicker
        applyThemeSync()

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun applyThemeSync() {
        // Baca tema dari DataStore secara blocking (hanya di onCreate)
        val prefs = androidx.datastore.preferences.core.preferencesOf()
        val dataStore = SettingsDataStore(this)

        lifecycleScope.launch {
            dataStore.theme.collect { theme ->
                when (theme) {
                    "dark" -> AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_YES
                    )
                    "light" -> AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_NO
                    )
                    else -> AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_YES
                    )
                }
            }
        }
    }
}