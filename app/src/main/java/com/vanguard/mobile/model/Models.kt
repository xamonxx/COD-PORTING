package com.vanguard.mobile.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class EngineMode { CAMPAIGN, MULTIPLAYER }

@Serializable
enum class RendererBackend { VULKAN, GLES }

@Serializable
enum class PerformanceProfile { QUALITY, BALANCED, PERFORMANCE, ADAPTIVE_120 }

@Serializable
data class LaunchRequest(
    val schemaVersion: Int = 1,
    val mode: EngineMode,
    val assetRoot: String,
    val renderer: RendererBackend = RendererBackend.VULKAN,
    val performanceProfile: PerformanceProfile = PerformanceProfile.ADAPTIVE_120,
    val renderScale: Float = 0.85f,
    val refreshRateHz: Int = 120,
    val language: String = "en",
    val controlProfileId: String = "default-touch",
    val mission: String? = null,
    val server: String? = null,
    val safeArguments: List<String> = emptyList(),
)

sealed interface EngineEvent {
    data class Progress(val stage: String, val fraction: Float) : EngineEvent
    data class Warning(val message: String) : EngineEvent
    data class FrameTiming(val fps: Float, val frameTimeMs: Float) : EngineEvent
    data class Log(val level: String, val message: String) : EngineEvent
    data class FatalRecovery(val reason: String, val fallback: RendererBackend) : EngineEvent
}

@Serializable
data class AssetManifest(
    val schemaVersion: Int = 1,
    val sourceLabel: String,
    val importedAtEpochMs: Long,
    val totalBytes: Long,
    val fileCount: Int,
    val hasCampaign: Boolean,
    val hasMultiplayer: Boolean,
    val missingRequiredFiles: List<String> = emptyList(),
)

@Serializable
data class ControlProfile(
    val schemaVersion: Int = 1,
    val id: String,
    val name: String,
    val opacity: Float = 0.8f,
    val controls: List<TouchControl> = defaultControls(),
) {
    companion object {
        fun defaultControls() = listOf(
            TouchControl("move", "Move", ControlKind.STICK, 0.15f, 0.72f, 0.22f),
            TouchControl("aim", "Aim", ControlKind.AIM_ZONE, 0.69f, 0.48f, 0.50f),
            TouchControl("fire", "Fire", ControlKind.ACTION, 0.88f, 0.55f, 0.12f),
            TouchControl("jump", "Jump", ControlKind.ACTION, 0.79f, 0.73f, 0.10f),
            TouchControl("reload", "Reload", ControlKind.ACTION, 0.90f, 0.78f, 0.10f),
        )
    }
}

@Serializable
data class TouchControl(
    val id: String,
    val label: String,
    val kind: ControlKind,
    val x: Float,
    val y: Float,
    val size: Float,
    val visibleWithGamepad: Boolean = false,
)

@Serializable
enum class ControlKind { STICK, AIM_ZONE, ACTION, COMBO, KEY }

@Serializable
data class GraphicsProfile(
    val schemaVersion: Int = 1,
    val renderer: RendererBackend = RendererBackend.VULKAN,
    val performance: PerformanceProfile = PerformanceProfile.ADAPTIVE_120,
    val targetRefreshHz: Int = 120,
    val minimumRefreshHz: Int = 60,
    val renderScale: Float = 0.85f,
    val dynamicResolution: Boolean = true,
)

@Serializable
data class DriverCatalog(
    val schemaVersion: Int = 1,
    val sequence: Long,
    val validFromEpochMs: Long,
    val validUntilEpochMs: Long,
    val entries: List<DriverEntry>,
    val signatureBase64: String,
)

@Serializable
data class DriverEntry(
    val id: String,
    val displayName: String,
    val family: String,
    val minimumApi: Int,
    val gpuPatterns: List<String>,
    val downloadUrl: String,
    val sha256: String,
    @SerialName("library_name") val libraryName: String,
)

data class ServerAddress(val host: String, val port: Int) {
    override fun toString(): String = if (host.contains(':')) "[$host]:$port" else "$host:$port"

    companion object {
        private const val DEFAULT_PORT = 28960

        fun parse(input: String): ServerAddress? {
            val value = input.trim()
            if (value.isEmpty() || value.length > 255 || value.any { it.isWhitespace() }) return null

            if (value.startsWith("[")) {
                val close = value.indexOf(']')
                if (close <= 1) return null
                val host = value.substring(1, close)
                val suffix = value.substring(close + 1)
                val port = if (suffix.isEmpty()) DEFAULT_PORT else {
                    if (!suffix.startsWith(':')) return null
                    suffix.drop(1).toIntOrNull() ?: return null
                }
                return takeIfValid(host, port)
            }

            val colonCount = value.count { it == ':' }
            if (colonCount > 1) return takeIfValid(value, DEFAULT_PORT)
            val host = value.substringBeforeLast(':')
            val port = if (colonCount == 1) value.substringAfterLast(':').toIntOrNull() ?: return null else DEFAULT_PORT
            return takeIfValid(host, port)
        }

        private fun takeIfValid(host: String, port: Int): ServerAddress? {
            if (host.isBlank() || port !in 1..65535) return null
            if (!host.matches(Regex("^[A-Za-z0-9._:%-]+$"))) return null
            return ServerAddress(host, port)
        }
    }
}
