package com.vanguard.mobile.performance

import com.vanguard.mobile.model.RendererBackend
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RendererRecoveryPolicyTest {
    @Test fun vulkanCrashRequestsGlesFallback() {
        val state = RendererRecoveryPolicy.afterRendererCrash(RendererBackend.VULKAN)
        assertEquals(RendererBackend.GLES, state.pendingFallback)
        assertEquals(1, state.crashCount)
    }

    @Test fun glesCrashDoesNotLoopBackToVulkan() {
        val state = RendererRecoveryPolicy.afterRendererCrash(RendererBackend.GLES)
        assertNull(state.pendingFallback)
    }

    @Test fun successfulFramesClearCrashState() {
        assertEquals(RendererRecoveryState(), RendererRecoveryPolicy.afterSuccessfulFrames())
    }
}
