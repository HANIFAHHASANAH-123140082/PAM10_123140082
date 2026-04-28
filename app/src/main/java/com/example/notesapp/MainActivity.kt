package com.example.notesapp

import android.content.Intent
import android.os.Bundle
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
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

        applyThemeSync()

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val btnAIChat = findViewById<ExtendedFloatingActionButton>(R.id.btnAIChat)
        btnAIChat.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            startActivity(intent)
        }
    }

    private fun applyThemeSync() {
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