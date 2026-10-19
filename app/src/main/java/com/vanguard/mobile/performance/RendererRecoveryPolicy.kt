package com.vanguard.mobile.performance

import com.vanguard.mobile.model.RendererBackend

data class RendererRecoveryState(
    val failedBackend: RendererBackend? = null,
    val pendingFallback: RendererBackend? = null,
    val crashCount: Int = 0,
)

object RendererRecoveryPolicy {
    fun afterRendererCrash(
        backend: RendererBackend,
        previous: RendererRecoveryState = RendererRecoveryState(),
    ): RendererRecoveryState = when (backend) {
        RendererBackend.VULKAN -> RendererRecoveryState(
            failedBackend = backend,
            pendingFallback = RendererBackend.GLES,
            crashCount = previous.crashCount + 1,
        )
        RendererBackend.GLES -> RendererRecoveryState(
            failedBackend = backend,
            pendingFallback = null,
            crashCount = previous.crashCount + 1,
        )
    }

    fun afterSuccessfulFrames(): RendererRecoveryState = RendererRecoveryState()
}
