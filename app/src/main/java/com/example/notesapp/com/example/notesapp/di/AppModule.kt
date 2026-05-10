package com.example.notesapp.di

import com.example.notesapp.data.NoteRepository
import com.example.notesapp.data.createDatabase
import com.example.notesapp.network.NetworkMonitor
import com.example.notesapp.platform.AndroidDeviceInfo
import com.example.notesapp.platform.DeviceInfo
import com.example.notesapp.ui.notes.NotesViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

// MODULE 1: Data layer
val dataModule = module {
    single { createDatabase(androidContext()) }
    single { NoteRepository(get()) }
    single<DeviceInfo> { AndroidDeviceInfo(androidContext()) }
    single { NetworkMonitor(androidContext()) }
}

// MODULE 2: ViewModel layer
val viewModelModule = module {
    viewModel { NotesViewModel(get()) }
}

// Gabungan semua module
val allModules = listOf(dataModule, viewModelModule)