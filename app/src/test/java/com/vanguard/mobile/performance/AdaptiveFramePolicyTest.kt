package com.vanguard.mobile.performance

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AdaptiveFramePolicyTest {
    private val policy = AdaptiveFramePolicy()

    @Test fun keeps120WhenBudgetIsMet() {
        val result = policy.decide(8.2f, ThermalState.NORMAL, 0.85f)
        assertEquals(120, result.refreshRateHz)
        assertTrue(result.renderScale > 0.85f)
    }

    @Test fun stepsDownWithoutRestart() {
        assertEquals(90, policy.decide(10.5f, ThermalState.NORMAL, 0.85f).refreshRateHz)
        assertEquals(60, policy.decide(18f, ThermalState.MODERATE, 0.80f).refreshRateHz)
    }

    @Test fun thermalLimitWins() {
        val result = policy.decide(7f, ThermalState.CRITICAL, 1f)
        assertEquals(60, result.refreshRateHz)
        assertTrue(result.renderScale <= 0.70f)
    }
}
