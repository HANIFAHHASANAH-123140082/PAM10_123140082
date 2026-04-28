package com.example.notesapp

import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.notesapp.databinding.ActivityChatBinding
import kotlinx.coroutines.launch

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var geminiHelper: GeminiHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val apiKey = BuildConfig.GROQ_API_KEY
        geminiHelper = GeminiHelper(apiKey)

        addBubble("Halo! Saya AI Assistant kamu 😊\nAda yang bisa saya bantu?", isUser = false)

        binding.btnSend.setOnClickListener {
            val msg = binding.etMessage.text.toString().trim()
            if (msg.isEmpty()) return@setOnClickListener
            sendMessage(msg)
        }
    }

    private fun sendMessage(message: String) {
        addBubble(message, isUser = true)
        binding.etMessage.setText("")
        binding.layoutTyping.visibility = View.VISIBLE
        binding.btnSend.isEnabled = false

        lifecycleScope.launch {
            val response = geminiHelper.sendMessage(message)
            binding.layoutTyping.visibility = View.GONE
            binding.btnSend.isEnabled = true
            addBubble(response, isUser = false)
        }
    }

    private fun addBubble(text: String, isUser: Boolean) {
        val isDark = (resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

        val bubble = TextView(this).apply {
            this.text = text
            textSize = 15f
            setPadding(32, 20, 32, 20)
            setTextColor(
                if (isUser) Color.WHITE
                else if (isDark) Color.WHITE
                else Color.parseColor("#1A1A2E")
            )
            background = createBubbleBackground(isUser, isDark)
            maxWidth = (resources.displayMetrics.widthPixels * 0.78).toInt()
        }

        val wrapper = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = if (isUser) Gravity.END else Gravity.START
            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 8, 0, 8)
            layoutParams = params
        }

        if (!isUser) {
            val aiLabel = TextView(this).apply {  // ← nama berbeda
                this.text = "🤖"
                textSize = 20f
                setPadding(0, 16, 8, 0)
            }
            wrapper.addView(aiLabel)
        }

        wrapper.addView(bubble)

        if (isUser) {
            val userLabel = TextView(this).apply {  // ← nama berbeda
                this.text = "👤"
                textSize = 20f
                setPadding(8, 16, 0, 0)
            }
            wrapper.addView(userLabel)
        }

        binding.chatContainer.addView(wrapper)

        binding.scrollView.post {
            binding.scrollView.fullScroll(View.FOCUS_DOWN)
        }
    }

    private fun createBubbleBackground(isUser: Boolean, isDark: Boolean): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadii = if (isUser)
                floatArrayOf(24f, 24f, 4f, 4f, 24f, 24f, 24f, 24f)
            else
                floatArrayOf(4f, 4f, 24f, 24f, 24f, 24f, 24f, 24f)
            setColor(
                when {
                    isUser -> Color.parseColor("#6200EE")
                    isDark -> Color.parseColor("#2D2D3F")
                    else   -> Color.parseColor("#F0EDFF")
                }
            )
        }
    }
}