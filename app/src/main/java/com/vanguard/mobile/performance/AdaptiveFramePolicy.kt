package com.vanguard.mobile.performance

enum class ThermalState { NORMAL, MODERATE, SEVERE, CRITICAL }

data class FrameDecision(
    val refreshRateHz: Int,
    val renderScale: Float,
    val reason: String,
)

class AdaptiveFramePolicy(
    private val preferredHz: Int = 120,
    private val minimumHz: Int = 60,
) {
    fun decide(
        averageFrameTimeMs: Float,
        thermalState: ThermalState,
        currentScale: Float,
    ): FrameDecision {
        require(preferredHz in setOf(60, 90, 120, 144))
        require(minimumHz in setOf(60, 90))
        val scale = currentScale.coerceIn(0.60f, 1.0f)

        if (thermalState >= ThermalState.CRITICAL) {
            return FrameDecision(60, minOf(scale, 0.70f), "critical thermal limit")
        }
        if (thermalState >= ThermalState.SEVERE) {
            return FrameDecision(maxOf(minimumHz, 60), minOf(scale, 0.75f), "thermal protection")
        }

        val preferredBudget = 1_000f / preferredHz
        if (averageFrameTimeMs <= preferredBudget * 1.08f) {
            return FrameDecision(preferredHz, (scale + 0.025f).coerceAtMost(1.0f), "preferred frame budget met")
        }
        if (averageFrameTimeMs <= 11.6f && preferredHz >= 90) {
            return FrameDecision(90, (scale - 0.05f).coerceAtLeast(0.65f), "adapted for stable 90 Hz")
        }
        return FrameDecision(60, (scale - 0.08f).coerceAtLeast(0.60f), "adapted for stable 60 Hz")
    }
}
