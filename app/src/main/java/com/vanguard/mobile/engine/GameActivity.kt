package com.vanguard.mobile.engine

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.vanguard.mobile.model.LaunchRequest
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class GameActivity : Activity() {
    private lateinit var bridge: NativeBridge

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.getInsetsController(window, window.decorView)
            .hide(WindowInsetsCompat.Type.systemBars())
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

        val encoded = intent.getStringExtra(EXTRA_REQUEST)
        val request = encoded?.let { runCatching { Json.decodeFromString<LaunchRequest>(it) }.getOrNull() }
        if (request == null) {
            showMessage("Invalid launch request")
            return
        }

        bridge = NativeBridge(this)
        val initialized = bridge.initialize(request.mode)
        val started = initialized && bridge.start(request)
        if (!started) {
            showMessage(
                "Native ${request.mode.name.lowercase()} runtime contract is ready, " +
                    "but the KisakCOD gameplay core has not been integrated yet.",
            )
        } else {
            showMessage("Starting ${request.mode.name.lowercase()} runtime…")
        }
    }

    override fun onPause() {
        if (::bridge.isInitialized) bridge.pause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        if (::bridge.isInitialized) bridge.resume()
    }

    override fun onDestroy() {
        if (::bridge.isInitialized) bridge.requestStop()
        super.onDestroy()
    }

    private fun showMessage(message: String) {
        val text = TextView(this).apply {
            setTextColor(Color.rgb(232, 238, 230))
            textSize = 18f
            gravity = Gravity.CENTER
            setPadding(64, 64, 64, 64)
            text = message
        }
        setContentView(FrameLayout(this).apply {
            setBackgroundColor(Color.rgb(11, 15, 18))
            addView(text, FrameLayout.LayoutParams(-1, -1))
        })
    }

    companion object { const val EXTRA_REQUEST = "launch_request" }
}
