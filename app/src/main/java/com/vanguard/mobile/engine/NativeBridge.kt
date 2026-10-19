package com.vanguard.mobile.engine

import android.content.Context
import com.vanguard.mobile.model.EngineEvent
import com.vanguard.mobile.model.EngineMode
import com.vanguard.mobile.model.LaunchRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal object CampaignNative {
    val available = runCatching { System.loadLibrary("vanguard_sp") }.isSuccess
    external fun initialize(filesDir: String, cacheDir: String): Boolean
    external fun start(requestJson: String): Boolean
    external fun pause()
    external fun resume()
    external fun stop()
    external fun input(type: Int, code: Int, x: Float, y: Float, value: Float)
    external fun pollEvent(): String?
}

internal object MultiplayerNative {
    val available = runCatching { System.loadLibrary("vanguard_mp") }.isSuccess
    external fun initialize(filesDir: String, cacheDir: String): Boolean
    external fun start(requestJson: String): Boolean
    external fun pause()
    external fun resume()
    external fun stop()
    external fun input(type: Int, code: Int, x: Float, y: Float, value: Float)
    external fun pollEvent(): String?
}

class NativeBridge(private val context: Context) {
    private val json = Json { encodeDefaults = true }
    private var activeMode: EngineMode? = null

    fun initialize(mode: EngineMode): Boolean {
        val files = context.filesDir.absolutePath
        val cache = context.cacheDir.absolutePath
        val initialized = when (mode) {
            EngineMode.CAMPAIGN -> CampaignNative.available && CampaignNative.initialize(files, cache)
            EngineMode.MULTIPLAYER -> MultiplayerNative.available && MultiplayerNative.initialize(files, cache)
        }
        activeMode = mode.takeIf { initialized }
        return initialized
    }

    fun start(request: LaunchRequest): Boolean {
        require(request.mode == activeMode) { "Initialize the requested engine mode first" }
        validate(request)
        val payload = json.encodeToString(request)
        return when (request.mode) {
            EngineMode.CAMPAIGN -> CampaignNative.start(payload)
            EngineMode.MULTIPLAYER -> MultiplayerNative.start(payload)
        }
    }

    fun pause() = when (activeMode) {
        EngineMode.CAMPAIGN -> CampaignNative.pause()
        EngineMode.MULTIPLAYER -> MultiplayerNative.pause()
        null -> Unit
    }

    fun resume() = when (activeMode) {
        EngineMode.CAMPAIGN -> CampaignNative.resume()
        EngineMode.MULTIPLAYER -> MultiplayerNative.resume()
        null -> Unit
    }

    fun requestStop() = when (activeMode) {
        EngineMode.CAMPAIGN -> CampaignNative.stop()
        EngineMode.MULTIPLAYER -> MultiplayerNative.stop()
        null -> Unit
    }.also { activeMode = null }

    fun submitInput(type: Int, code: Int, x: Float, y: Float, value: Float) = when (activeMode) {
        EngineMode.CAMPAIGN -> CampaignNative.input(type, code, x, y, value)
        EngineMode.MULTIPLAYER -> MultiplayerNative.input(type, code, x, y, value)
        null -> Unit
    }

    fun events(): Flow<EngineEvent> = flow {
        while (activeMode != null) {
            val encoded = when (activeMode) {
                EngineMode.CAMPAIGN -> CampaignNative.pollEvent()
                EngineMode.MULTIPLAYER -> MultiplayerNative.pollEvent()
                null -> null
            }
            if (encoded != null) emit(parseEvent(encoded)) else delay(16)
        }
    }

    private fun parseEvent(value: String): EngineEvent {
        val parts = value.split('|', limit = 3)
        return when (parts.firstOrNull()) {
            "warning" -> EngineEvent.Warning(parts.getOrElse(1) { "Unknown warning" })
            "progress" -> EngineEvent.Progress(
                parts.getOrElse(1) { "runtime" },
                parts.getOrNull(2)?.toFloatOrNull()?.coerceIn(0f, 1f) ?: 0f,
            )
            "log" -> EngineEvent.Log(parts.getOrElse(1) { "info" }, parts.getOrElse(2) { "" })
            else -> EngineEvent.Log("debug", value)
        }
    }

    private fun validate(request: LaunchRequest) {
        require(request.schemaVersion == 1)
        require(request.assetRoot.isNotBlank() && !request.assetRoot.contains('\u0000'))
        require(request.refreshRateHz in setOf(60, 90, 120, 144))
        require(request.renderScale in 0.60f..1.0f)
        require(request.safeArguments.size <= 32)
        request.safeArguments.forEach {
            require(it.length <= 128 && !it.contains('\n') && !it.contains('\r'))
        }
    }
}
