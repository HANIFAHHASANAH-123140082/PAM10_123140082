package com.example.notesapp

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class GeminiHelper(private val apiKey: String) {

    suspend fun sendMessage(userMessage: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("https://api.groq.com/openai/v1/chat/completions")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json")
                conn.setRequestProperty("Authorization", "Bearer $apiKey")
                conn.connectTimeout = 15000
                conn.readTimeout = 15000
                conn.doOutput = true

                val body = JSONObject().apply {
                    put("model", "llama-3.3-70b-versatile")
                    put("messages", JSONArray().apply {
                        put(JSONObject().apply {
                            put("role", "system")
                            put("content", "Kamu adalah asisten pintar dalam aplikasi Notes. Jawab dengan bahasa Indonesia yang ramah dan ringkas.")
                        })
                        put(JSONObject().apply {
                            put("role", "user")
                            put("content", userMessage)
                        })
                    })
                }

                val writer = OutputStreamWriter(conn.outputStream)
                writer.write(body.toString())
                writer.flush()
                writer.close()

                val responseCode = conn.responseCode

                // ✅ Baca errorStream kalau response bukan 200
                val responseText = if (responseCode == 200) {
                    conn.inputStream.bufferedReader().readText()
                } else {
                    val error = conn.errorStream?.bufferedReader()?.readText() ?: "Unknown error"
                    return@withContext "Error $responseCode: $error"
                }

                val json = JSONObject(responseText)
                json.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")

            } catch (e: Exception) {
                "Error: ${e.javaClass.simpleName} - ${e.message}"
            }
        }
    }
}