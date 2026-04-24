package com.example.notesapp.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsDataStore(private val context: Context) {

    companion object {
        val THEME_KEY = stringPreferencesKey("theme")
        val SORT_ORDER_KEY = stringPreferencesKey("sort")
    }

    val theme: Flow<String> = context.dataStore.data
        .map { prefs -> prefs[THEME_KEY] ?: "dark" }

    val sortOrder: Flow<String> = context.dataStore.data
        .map { prefs -> prefs[SORT_ORDER_KEY] ?: "newest" }

    suspend fun setTheme(theme: String) {
        context.dataStore.edit { it[THEME_KEY] = theme }
    }

    suspend fun setSortOrder(order: String) {
        context.dataStore.edit { it[SORT_ORDER_KEY] = order }
    }
}