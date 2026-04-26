package com.example.notesapp.platform


interface DeviceInfo {
    fun getDeviceName(): String
    fun getOsVersion(): String
    fun getAppVersion(): String
}