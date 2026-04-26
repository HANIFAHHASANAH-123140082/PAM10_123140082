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

val appModule = module {

    // Database
    single { createDatabase(androidContext()) }

    // Data
    single { NoteRepository(get()) }

    // Platform
    single<DeviceInfo> { AndroidDeviceInfo(androidContext()) }
    single { NetworkMonitor(androidContext()) }

    // ViewModel
    viewModel { NotesViewModel(get()) }
}