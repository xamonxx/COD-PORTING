package com.vanguard.mobile.data

data class AssetLayoutStatus(
    val hasCampaign: Boolean,
    val hasMultiplayer: Boolean,
    val missingRequiredFiles: List<String>,
)

object AssetLayoutValidator {
    fun validate(relativePaths: Collection<String>): AssetLayoutStatus {
        val normalized = relativePaths.map { it.replace('\\', '/').trimStart('/').lowercase() }.toSet()
        val hasMain = normalized.any { it.startsWith("main/") }
        val hasZone = normalized.any { it.endsWith("common.ff") } && normalized.any { it.endsWith("ui.ff") }
        val hasMp = normalized.any {
            it.endsWith("iw_00.iwd") || it.endsWith("code_post_gfx_mp.ff")
        }
        val missing = buildList {
            if (!hasMain) add("main/")
            if (!hasZone) add("zone/*/common.ff and ui.ff")
        }
        return AssetLayoutStatus(
            hasCampaign = hasMain && hasZone,
            hasMultiplayer = hasMain && hasMp,
            missingRequiredFiles = missing,
        )
    }
}
