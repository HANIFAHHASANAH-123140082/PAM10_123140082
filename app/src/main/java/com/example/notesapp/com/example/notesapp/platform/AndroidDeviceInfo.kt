package com.example.notesapp.platform

import android.content.Context
import android.os.Build

// Simulasi "actual" di KMM — implementasi khusus Android
class AndroidDeviceInfo(private val context: Context) : DeviceInfo {

    override fun getDeviceName(): String =
        "${Build.MANUFACTURER} ${Build.MODEL}"

    override fun getOsVersion(): String =
        "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"

    override fun getAppVersion(): String {
        return try {
            context.packageManager
                .getPackageInfo(context.packageName, 0)
                .versionName ?: "1.0.0"
        } catch (e: Exception) {
            "1.0.0"
        }
    }
}