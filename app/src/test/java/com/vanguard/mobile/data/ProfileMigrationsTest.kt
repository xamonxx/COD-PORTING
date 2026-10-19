package com.vanguard.mobile.data

import com.vanguard.mobile.model.ControlProfile
import com.vanguard.mobile.model.GraphicsProfile
import com.vanguard.mobile.model.TouchControl
import com.vanguard.mobile.model.ControlKind
import org.junit.Assert.assertEquals
import org.junit.Test

class ProfileMigrationsTest {
    @Test fun graphicsMigrationIsIdempotent() {
        val migrated = ProfileMigrations.graphics(GraphicsProfile(renderScale = 2f, targetRefreshHz = 75))
        assertEquals(migrated, ProfileMigrations.graphics(migrated))
        assertEquals(1f, migrated.renderScale)
        assertEquals(120, migrated.targetRefreshHz)
    }

    @Test fun controlMigrationPreservesIdentityAndBoundsGeometry() {
        val profile = ControlProfile(
            id = "mine",
            name = "Mine",
            opacity = 2f,
            controls = listOf(TouchControl("fire", "Fire", ControlKind.ACTION, 2f, -1f, 3f)),
        )
        val migrated = ProfileMigrations.controls(profile)
        assertEquals("mine", migrated.id)
        assertEquals(migrated, ProfileMigrations.controls(migrated))
        assertEquals(1f, migrated.opacity)
        assertEquals(1f, migrated.controls.single().x)
    }
}
