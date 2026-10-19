package com.vanguard.mobile.data

import com.vanguard.mobile.model.ControlProfile
import com.vanguard.mobile.model.GraphicsProfile

object ProfileMigrations {
    fun graphics(profile: GraphicsProfile): GraphicsProfile = profile.copy(
        schemaVersion = 1,
        targetRefreshHz = profile.targetRefreshHz.takeIf { it in setOf(60, 90, 120, 144) } ?: 120,
        minimumRefreshHz = profile.minimumRefreshHz.takeIf { it in setOf(60, 90) } ?: 60,
        renderScale = profile.renderScale.coerceIn(0.60f, 1.0f),
    )

    fun controls(profile: ControlProfile): ControlProfile = profile.copy(
        schemaVersion = 1,
        opacity = profile.opacity.coerceIn(0.25f, 1.0f),
        controls = profile.controls.map { control ->
            control.copy(
                x = control.x.coerceIn(0f, 1f),
                y = control.y.coerceIn(0f, 1f),
                size = control.size.coerceIn(0.04f, 0.75f),
            )
        },
    )
}
