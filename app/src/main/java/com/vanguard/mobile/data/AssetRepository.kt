package com.vanguard.mobile.data

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.vanguard.mobile.model.AssetManifest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileOutputStream

data class ImportProgress(val copiedFiles: Int, val totalFiles: Int, val currentName: String)

sealed interface ImportResult {
    data class Success(val root: File, val manifest: AssetManifest) : ImportResult
    data class Failure(val reason: String) : ImportResult
}

class AssetRepository(private val context: Context) {
    private val json = Json { prettyPrint = true; encodeDefaults = true }
    private val assetRoot = File(context.getExternalFilesDir(null), "game-data")

    fun currentManifest(): AssetManifest? {
        val file = File(assetRoot, "asset-manifest.json")
        if (!file.isFile) return null
        return runCatching { json.decodeFromString<AssetManifest>(file.readText()) }.getOrNull()
    }

    suspend fun importFromTree(uri: Uri, onProgress: (ImportProgress) -> Unit): ImportResult = withContext(Dispatchers.IO) {
        val root = DocumentFile.fromTreeUri(context, uri)
            ?: return@withContext ImportResult.Failure("Folder cannot be opened")
        if (!root.canRead()) return@withContext ImportResult.Failure("Folder is not readable")

        val files = mutableListOf<Pair<DocumentFile, String>>()
        val scanError = scan(root, "", files, 25_000)
        if (scanError != null) return@withContext ImportResult.Failure(scanError)

        val layout = AssetLayoutValidator.validate(files.map { it.second })
        if (layout.missingRequiredFiles.isNotEmpty()) {
            return@withContext ImportResult.Failure(
                "Required licensed game files are missing: ${layout.missingRequiredFiles.joinToString()}",
            )
        }

        assetRoot.mkdirs()
        var totalBytes = 0L
        files.forEachIndexed { index, (document, relative) ->
            val safeRelative = relative.replace('\\', '/').trimStart('/')
            if (safeRelative.contains("../") || safeRelative.startsWith("META-INF/")) return@forEachIndexed
            val output = File(assetRoot, safeRelative).canonicalFile
            if (!output.path.startsWith(assetRoot.canonicalPath + File.separator)) {
                return@withContext ImportResult.Failure("Unsafe path rejected: $safeRelative")
            }
            onProgress(ImportProgress(index, files.size, document.name ?: safeRelative))
            output.parentFile?.mkdirs()
            val expected = document.length()
            if (output.isFile && expected > 0 && output.length() == expected) {
                totalBytes += output.length()
                return@forEachIndexed
            }
            val input = context.contentResolver.openInputStream(document.uri)
                ?: return@withContext ImportResult.Failure("Cannot read ${document.name}")
            input.use { source -> FileOutputStream(output).use { source.copyTo(it, 256 * 1024) } }
            totalBytes += output.length()
        }

        val manifest = AssetManifest(
            sourceLabel = root.name ?: "Selected folder",
            importedAtEpochMs = System.currentTimeMillis(),
            totalBytes = totalBytes,
            fileCount = files.size,
            hasCampaign = layout.hasCampaign,
            hasMultiplayer = layout.hasMultiplayer,
        )
        File(assetRoot, "asset-manifest.json").writeText(json.encodeToString(manifest))
        ImportResult.Success(assetRoot, manifest)
    }

    private fun scan(
        node: DocumentFile,
        relative: String,
        output: MutableList<Pair<DocumentFile, String>>,
        limit: Int,
    ): String? {
        if (output.size >= limit) return "Folder exceeds the $limit file safety limit"
        val children = runCatching { node.listFiles() }.getOrElse { return "Cannot list ${node.name}" }
        for (child in children) {
            val name = child.name ?: continue
            if (name == "." || name == ".." || name.contains('/') || name.contains('\\')) continue
            val path = if (relative.isEmpty()) name else "$relative/$name"
            when {
                child.isDirectory -> scan(child, path, output, limit)?.let { return it }
                child.isFile -> output += child to path
            }
        }
        return null
    }
}
