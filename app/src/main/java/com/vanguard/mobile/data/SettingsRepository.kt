package com.vanguard.mobile.data

import android.content.Context
import com.vanguard.mobile.model.ControlProfile
import com.vanguard.mobile.model.GraphicsProfile
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SettingsRepository(context: Context) {
    private val preferences = context.getSharedPreferences("vanguard-settings-v1", Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

    fun graphics(): GraphicsProfile = ProfileMigrations.graphics(decode("graphics") ?: GraphicsProfile())
    fun controls(): ControlProfile = ProfileMigrations.controls(
        decode("controls") ?: ControlProfile(
            id = "default-touch",
            name = "Default touch",
        ),
    )

    fun saveGraphics(profile: GraphicsProfile) = save("graphics", ProfileMigrations.graphics(profile))
    fun saveControls(profile: ControlProfile) = save("controls", ProfileMigrations.controls(profile))

    private inline fun <reified T> decode(key: String): T? = preferences.getString(key, null)?.let {
        runCatching { json.decodeFromString<T>(it) }.getOrNull()
    }

    private inline fun <reified T> save(key: String, value: T) {
        preferences.edit().putString(key, json.encodeToString(value)).apply()
    }
}
