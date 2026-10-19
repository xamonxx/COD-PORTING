package com.vanguard.mobile.data

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AssetLayoutValidatorTest {
    @Test fun recognizesCampaignAndMultiplayerLayouts() {
        val status = AssetLayoutValidator.validate(
            listOf("main/iw_00.iwd", "zone/english/common.ff", "zone/english/ui.ff"),
        )
        assertTrue(status.hasCampaign)
        assertTrue(status.hasMultiplayer)
        assertTrue(status.missingRequiredFiles.isEmpty())
    }

    @Test fun rejectsUnrelatedFolder() {
        val status = AssetLayoutValidator.validate(listOf("downloads/readme.txt"))
        assertFalse(status.hasCampaign)
        assertFalse(status.hasMultiplayer)
        assertTrue(status.missingRequiredFiles.isNotEmpty())
    }
}
