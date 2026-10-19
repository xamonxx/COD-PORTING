package com.vanguard.mobile.ui

import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vanguard.mobile.data.AssetRepository
import com.vanguard.mobile.data.ImportProgress
import com.vanguard.mobile.data.ImportResult
import com.vanguard.mobile.data.SettingsRepository
import com.vanguard.mobile.engine.GameActivity
import com.vanguard.mobile.model.ControlProfile
import com.vanguard.mobile.model.EngineMode
import com.vanguard.mobile.model.GraphicsProfile
import com.vanguard.mobile.model.LaunchRequest
import com.vanguard.mobile.model.PerformanceProfile
import com.vanguard.mobile.model.RendererBackend
import com.vanguard.mobile.model.ServerAddress
import com.vanguard.mobile.ui.theme.VanguardColors
import java.io.File
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private enum class Screen(val label: String, val code: String) {
    HOME("Command", "01"),
    LIBRARY("Library", "02"),
    CAMPAIGN("Campaign", "03"),
    MULTIPLAYER("Network", "04"),
    CONTROLS("Controls", "05"),
    GRAPHICS("Performance", "06"),
    DRIVERS("Drivers", "07"),
    DIAGNOSTICS("Diagnostics", "08"),
    ABOUT("About", "09"),
}

@Composable
fun VanguardApp() {
    var screen by remember { mutableStateOf(Screen.HOME) }
    Scaffold(containerColor = VanguardColors.Graphite) { padding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(12.dp),
        ) {
            NavigationPanel(screen, onSelect = { screen = it })
            Spacer(Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(18.dp))
                    .background(VanguardColors.GraphiteRaised),
            ) {
                when (screen) {
                    Screen.HOME -> HomeScreen(onNavigate = { screen = it })
                    Screen.LIBRARY -> LibraryScreen()
                    Screen.CAMPAIGN -> CampaignScreen()
                    Screen.MULTIPLAYER -> MultiplayerScreen()
                    Screen.CONTROLS -> ControlsScreen()
                    Screen.GRAPHICS -> GraphicsScreen()
                    Screen.DRIVERS -> DriversScreen()
                    Screen.DIAGNOSTICS -> DiagnosticsScreen()
                    Screen.ABOUT -> AboutScreen()
                }
            }
        }
    }
}

@Composable
private fun NavigationPanel(selected: Screen, onSelect: (Screen) -> Unit) {
    Column(
        modifier = Modifier
            .width(184.dp)
            .fillMaxHeight()
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF070A0C))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .background(VanguardColors.Lime),
                contentAlignment = Alignment.Center,
            ) {
                Text("V", color = Color(0xFF101600), fontWeight = FontWeight.Black, fontSize = 20.sp)
            }
            Spacer(Modifier.width(10.dp))
            Column {
                Text("VANGUARD", fontWeight = FontWeight.Black, letterSpacing = 1.sp, fontSize = 14.sp)
                Text("MOBILE / ARM64", color = VanguardColors.Muted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
            }
        }
        Spacer(Modifier.height(12.dp))
        LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            items(Screen.entries) { item ->
                val active = item == selected
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (active) VanguardColors.GraphiteSoft else Color.Transparent)
                        .clickable { onSelect(item) }
                        .heightIn(min = 48.dp)
                        .padding(horizontal = 10.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(item.code, color = if (active) VanguardColors.Lime else VanguardColors.Muted, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                    Spacer(Modifier.width(10.dp))
                    Text(item.label, color = if (active) VanguardColors.Ink else VanguardColors.Muted, fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal)
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        StatusPill("FOUNDATION BUILD", VanguardColors.Warning)
    }
}

@Composable
private fun HomeScreen(onNavigate: (Screen) -> Unit) {
    val context = LocalContext.current
    val assets = remember { AssetRepository(context).currentManifest() }
    Page("Command center", "Clean-room launcher / iQOO 15R performance target") {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            MetricCard("ASSET STATE", if (assets != null) "READY" else "NOT IMPORTED", if (assets != null) VanguardColors.Lime else VanguardColors.Warning, Modifier.weight(1f))
            MetricCard("RENDER PATH", "VULKAN → GLES", VanguardColors.Lime, Modifier.weight(1f))
            MetricCard("FRAME POLICY", "120 / 90 / 60", VanguardColors.Lime, Modifier.weight(1f))
        }
        Spacer(Modifier.height(12.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF10161A)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(Modifier.padding(22.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("TWO RUNTIMES. ONE SAFE ENTRY POINT.", color = VanguardColors.Lime, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("Campaign and multiplayer contracts are wired for ARM64.", fontWeight = FontWeight.Black, fontSize = 27.sp, lineHeight = 31.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("Import legally owned game data, validate it locally, then hand off a bounded request to the native runtime.", color = VanguardColors.Muted, fontSize = 14.sp)
                }
                Spacer(Modifier.width(20.dp))
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(onClick = { onNavigate(Screen.CAMPAIGN) }, enabled = assets?.hasCampaign == true, modifier = Modifier.height(52.dp)) { Text("OPEN CAMPAIGN") }
                    OutlinedButton(onClick = { onNavigate(Screen.MULTIPLAYER) }, enabled = assets?.hasMultiplayer == true, modifier = Modifier.height(52.dp)) { Text("OPEN NETWORK") }
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            ActionCard("IMPORT", "Select a licensed data folder", "No broad storage permission", { onNavigate(Screen.LIBRARY) }, Modifier.weight(1f))
            ActionCard("TUNE", "Adaptive 120 FPS", "Thermal-aware fallback", { onNavigate(Screen.GRAPHICS) }, Modifier.weight(1f))
            ActionCard("VERIFY", "Diagnostics & provenance", "No proprietary payloads", { onNavigate(Screen.DIAGNOSTICS) }, Modifier.weight(1f))
        }
    }
}

@Composable
private fun LibraryScreen() {
    val context = LocalContext.current
    val repository = remember { AssetRepository(context) }
    val scope = rememberCoroutineScope()
    var manifest by remember { mutableStateOf(repository.currentManifest()) }
    var status by remember { mutableStateOf("Choose the root folder of a legally owned installation.") }
    var progress by remember { mutableStateOf<ImportProgress?>(null) }
    var importing by remember { mutableStateOf(false) }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        if (uri != null) {
            runCatching { context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION) }
            importing = true
            scope.launch {
                when (val result = repository.importFromTree(uri) { progress = it }) {
                    is ImportResult.Success -> {
                        manifest = result.manifest
                        status = "Import complete: ${result.manifest.fileCount} files verified."
                    }
                    is ImportResult.Failure -> status = result.reason
                }
                importing = false
                progress = null
            }
        }
    }

    Page("Game library", "Storage Access Framework / resumable local copy") {
        InfoBanner("LEGAL DATA ONLY", "Vanguard Mobile never downloads or bundles commercial game assets. Select files you are licensed to use.", VanguardColors.Warning)
        Spacer(Modifier.height(12.dp))
        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF10161A)), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp)) {
                Text(if (manifest == null) "No validated library" else manifest!!.sourceLabel, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                Text(status, color = VanguardColors.Muted)
                progress?.let {
                    Spacer(Modifier.height(12.dp))
                    LinearProgressIndicator(progress = { if (it.totalFiles == 0) 0f else it.copiedFiles.toFloat() / it.totalFiles }, Modifier.fillMaxWidth())
                    Text(it.currentName, color = VanguardColors.Muted, maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 12.sp)
                }
                Spacer(Modifier.height(16.dp))
                Button(onClick = { picker.launch(null) }, enabled = !importing, modifier = Modifier.height(52.dp)) {
                    Text(if (importing) "IMPORTING…" else "SELECT GAME FOLDER")
                }
            }
        }
        manifest?.let {
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricCard("FILES", it.fileCount.toString(), VanguardColors.Lime, Modifier.weight(1f))
                MetricCard("CAMPAIGN", if (it.hasCampaign) "READY" else "MISSING", if (it.hasCampaign) VanguardColors.Lime else VanguardColors.Warning, Modifier.weight(1f))
                MetricCard("MULTIPLAYER", if (it.hasMultiplayer) "READY" else "PARTIAL", if (it.hasMultiplayer) VanguardColors.Lime else VanguardColors.Warning, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun CampaignScreen() {
    val context = LocalContext.current
    val manifest = remember { AssetRepository(context).currentManifest() }
    Page("Campaign", "Single-player vertical slice / first mission gate") {
        if (manifest?.hasCampaign != true) {
            InfoBanner("ASSETS REQUIRED", "Import a complete licensed data folder before starting campaign.", VanguardColors.Warning)
        }
        Spacer(Modifier.height(12.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items((1..21).toList()) { index ->
                val enabled = index == 1 && manifest?.hasCampaign == true
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF10161A))
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(index.toString().padStart(2, '0'), color = if (enabled) VanguardColors.Lime else VanguardColors.Muted, fontFamily = FontFamily.Monospace)
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Mission ${index.toString().padStart(2, '0')}", fontWeight = FontWeight.SemiBold)
                        Text(if (index == 1) "Vertical-slice target" else "Locked until compatibility verification", color = VanguardColors.Muted, fontSize = 12.sp)
                    }
                    Button(onClick = { launchGame(context, EngineMode.CAMPAIGN, mission = "mission-01") }, enabled = enabled) { Text(if (index == 1) "START" else "LOCKED") }
                }
            }
        }
    }
}

@Composable
private fun MultiplayerScreen() {
    val context = LocalContext.current
    val manifest = remember { AssetRepository(context).currentManifest() }
    var serverText by remember { mutableStateOf("192.168.1.20:28960") }
    val parsed = remember(serverText) { ServerAddress.parse(serverText) }
    Page("Network", "Private server vertical slice / default UDP 28960") {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF10161A)), modifier = Modifier.weight(1.4f)) {
                Column(Modifier.padding(20.dp)) {
                    Text("DIRECT CONNECT", color = VanguardColors.Lime, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = serverText,
                        onValueChange = { serverText = it.take(255) },
                        label = { Text("Host or IP") },
                        supportingText = { Text(if (parsed == null) "Invalid address" else "Resolved input: $parsed") },
                        isError = parsed == null,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = { launchGame(context, EngineMode.MULTIPLAYER, server = parsed.toString()) },
                        enabled = parsed != null && manifest?.hasMultiplayer == true,
                        modifier = Modifier.height(52.dp),
                    ) { Text("CONNECT") }
                }
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricCard("TRANSPORT", "UDP / PRIVATE", VanguardColors.Lime)
                MetricCard("ASSET PROFILE", if (manifest?.hasMultiplayer == true) "VALID" else "IMPORT REQUIRED", if (manifest?.hasMultiplayer == true) VanguardColors.Lime else VanguardColors.Warning)
                InfoBanner("SECURITY", "Only bounded host/port input is passed to native code.", VanguardColors.Lime)
            }
        }
    }
}

@Composable
private fun ControlsScreen() {
    val context = LocalContext.current
    val settings = remember { SettingsRepository(context) }
    var profile by remember { mutableStateOf(settings.controls()) }
    Page("Controls", "Touch editor / gamepad-aware visibility") {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1.5f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF080C0F))
                    .border(1.dp, VanguardColors.GraphiteSoft, RoundedCornerShape(16.dp)),
            ) {
                Canvas(Modifier.fillMaxSize()) {
                    profile.controls.forEach { control ->
                        val center = Offset(size.width * control.x, size.height * control.y)
                        val radius = size.minDimension * control.size * 0.42f
                        drawCircle(VanguardColors.Lime.copy(alpha = profile.opacity * 0.28f), radius, center)
                        drawCircle(VanguardColors.Lime.copy(alpha = 0.75f), radius, center, style = Stroke(width = 2.dp.toPx()))
                    }
                }
                Text("LIVE SAFE-AREA PREVIEW", Modifier.padding(16.dp), color = VanguardColors.Muted, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
            }
            Column(Modifier.weight(1f).fillMaxHeight()) {
                Text("PROFILE OPACITY", color = VanguardColors.Muted, fontSize = 12.sp)
                Slider(value = profile.opacity, onValueChange = { profile = profile.copy(opacity = it) }, valueRange = 0.25f..1f)
                Spacer(Modifier.height(8.dp))
                LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(profile.controls) { control ->
                        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF10161A))) {
                            Column(Modifier.padding(12.dp)) {
                                Text(control.label, fontWeight = FontWeight.SemiBold)
                                Text("${control.kind} · size ${(control.size * 100).toInt()}%", color = VanguardColors.Muted, fontSize = 11.sp)
                            }
                        }
                    }
                }
                Button(onClick = { settings.saveControls(profile) }, modifier = Modifier.fillMaxWidth().height(52.dp)) { Text("SAVE PROFILE") }
            }
        }
    }
}

@Composable
private fun GraphicsScreen() {
    val context = LocalContext.current
    val settings = remember { SettingsRepository(context) }
    var profile by remember { mutableStateOf(settings.graphics()) }
    var scale by remember { mutableFloatStateOf(profile.renderScale) }
    var refresh by remember { mutableIntStateOf(profile.targetRefreshHz) }
    Page("Performance", "iQOO 15R / Snapdragon 8 Gen 5 target") {
        InfoBanner("ADAPTIVE 120", "120 Hz is the target. The runtime may step down to 90 or 60 Hz for thermal protection and stable pacing.", VanguardColors.Lime)
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            SettingsCard("RENDERER", Modifier.weight(1f)) {
                ToggleRow("Vulkan primary", profile.renderer == RendererBackend.VULKAN) { profile = profile.copy(renderer = if (it) RendererBackend.VULKAN else RendererBackend.GLES) }
                Text("OpenGL ES remains the crash-recovery path.", color = VanguardColors.Muted, fontSize = 12.sp)
            }
            SettingsCard("DYNAMIC RESOLUTION", Modifier.weight(1f)) {
                Text("Internal scale ${(scale * 100).toInt()}%", fontWeight = FontWeight.SemiBold)
                Slider(value = scale, onValueChange = { scale = it }, valueRange = 0.60f..1f)
                ToggleRow("Automatic scaling", profile.dynamicResolution) { profile = profile.copy(dynamicResolution = it) }
            }
            SettingsCard("REFRESH TARGET", Modifier.weight(1f)) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(60, 90, 120, 144).forEach { hz ->
                        OutlinedButton(onClick = { refresh = hz }, colors = if (refresh == hz) ButtonDefaults.outlinedButtonColors(containerColor = VanguardColors.GraphiteSoft) else ButtonDefaults.outlinedButtonColors(), contentPadding = PaddingValues(horizontal = 10.dp)) { Text("$hz") }
                    }
                }
                Text("144 Hz remains experimental.", color = VanguardColors.Muted, fontSize = 12.sp)
            }
        }
        Spacer(Modifier.height(12.dp))
        Button(onClick = {
            profile = profile.copy(renderScale = scale, targetRefreshHz = refresh, performance = PerformanceProfile.ADAPTIVE_120)
            settings.saveGraphics(profile)
        }, modifier = Modifier.height(52.dp)) { Text("SAVE PERFORMANCE PROFILE") }
    }
}

@Composable
private fun DriversScreen() {
    Page("Driver hub", "System Vulkan first / signed external packages later") {
        InfoBanner("SAFE DEFAULT", "The system driver is always recoverable. External activation requires hash validation and an isolated presentation probe.", VanguardColors.Lime)
        Spacer(Modifier.height(12.dp))
        DriverRow("System Vulkan", "Active · recommended for iQOO 15R", "ACTIVE", VanguardColors.Lime)
        Spacer(Modifier.height(8.dp))
        DriverRow("Imported Turnip", "No verified package installed", "UNAVAILABLE", VanguardColors.Muted)
        Spacer(Modifier.height(8.dp))
        DriverRow("OpenGL ES", "Automatic renderer recovery", "STANDBY", VanguardColors.Warning)
        Spacer(Modifier.height(16.dp))
        OutlinedButton(onClick = {}, enabled = false, modifier = Modifier.height(52.dp)) { Text("IMPORT SIGNED DRIVER PACKAGE") }
        Text("Driver installation remains disabled until Ed25519 catalog verification and Vulkan probing are complete.", color = VanguardColors.Muted, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
    }
}

@Composable
private fun DiagnosticsScreen() {
    val context = LocalContext.current
    val assetManifest = remember { AssetRepository(context).currentManifest() }
    Page("Diagnostics", "Build provenance / capability boundaries") {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            MetricCard("DEVICE", "${Build.MANUFACTURER.uppercase()} ${Build.MODEL}", VanguardColors.Lime, Modifier.weight(1f))
            MetricCard("ANDROID", "API ${Build.VERSION.SDK_INT}", VanguardColors.Lime, Modifier.weight(1f))
            MetricCard("ABI", Build.SUPPORTED_ABIS.firstOrNull() ?: "UNKNOWN", VanguardColors.Lime, Modifier.weight(1f))
        }
        Spacer(Modifier.height(12.dp))
        InfoBanner("IMPLEMENTED", "Launcher UI, SAF import, versioned models, native ABI contract, ARM64 targets, adaptive frame policy, tests, and GitHub build/signing pipeline.", VanguardColors.Lime)
        Spacer(Modifier.height(8.dp))
        InfoBanner("NOT YET PLAYABLE", "KisakCOD gameplay code, renderer translation, audio backend, and device validation are not integrated in this foundation build.", VanguardColors.Warning)
        Spacer(Modifier.height(8.dp))
        InfoBanner("REFERENCE EVIDENCE", "APK SHA-256 a3b27a…5622c · REA evidence ev_9d3fa3…348a4d · static observation only.", VanguardColors.Muted)
        Spacer(Modifier.height(8.dp))
        InfoBanner("ASSET STATUS", assetManifest?.let { "${it.fileCount} files · campaign=${it.hasCampaign} · multiplayer=${it.hasMultiplayer}" } ?: "No imported assets", VanguardColors.Muted)
        Spacer(Modifier.height(8.dp))
        Text("Vanguard Mobile is GPL-3.0. It ships no Activision assets, reference APK code, Windows executables, Bink, or Miles binaries.", color = VanguardColors.Muted, fontSize = 12.sp)
    }
}

@Composable
private fun AboutScreen() {
    Page("About & licenses", "GPL-3.0-only / clean-room provenance") {
        InfoBanner(
            "VANGUARD MOBILE 0.1.0",
            "Independent Android ARM64 porting foundation. No commercial game data is included.",
            VanguardColors.Lime,
        )
        Spacer(Modifier.height(12.dp))
        SettingsCard("SOURCE LICENSE", Modifier.fillMaxWidth()) {
            Text("GNU General Public License version 3 only", fontWeight = FontWeight.Bold)
            Text("Corresponding source is published with each tagged release.", color = VanguardColors.Muted, fontSize = 12.sp)
        }
        Spacer(Modifier.height(8.dp))
        SettingsCard("PLANNED ENGINE BASELINE", Modifier.fillMaxWidth()) {
            Text("KisakCOD · GPL-3.0 · commit 8aadf94cc82795d6829ca06052996a5ec79001c3", fontFamily = FontFamily.Monospace, fontSize = 12.sp)
            Text("Not vendored or integrated in this foundation build.", color = VanguardColors.Warning, fontSize = 12.sp)
        }
        Spacer(Modifier.height(8.dp))
        SettingsCard("TRADEMARK NOTICE", Modifier.fillMaxWidth()) {
            Text("Not affiliated with Activision, Infinity Ward, APKVision, Qualcomm, or vivo/iQOO.", color = VanguardColors.Muted, fontSize = 12.sp)
        }
    }
}

@Composable
private fun Page(title: String, subtitle: String, content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Text(title.uppercase(), fontSize = 26.sp, fontWeight = FontWeight.Black, letterSpacing = 0.5.sp)
        Text(subtitle, color = VanguardColors.Muted, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
        Spacer(Modifier.height(18.dp))
        content()
    }
}

@Composable
private fun MetricCard(label: String, value: String, accent: Color, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = Color(0xFF10161A)), shape = RoundedCornerShape(14.dp)) {
        Column(Modifier.padding(15.dp)) {
            Text(label, color = VanguardColors.Muted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
            Spacer(Modifier.height(5.dp))
            Text(value, color = accent, fontWeight = FontWeight.Bold, fontSize = 17.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun ActionCard(code: String, title: String, body: String, action: () -> Unit, modifier: Modifier = Modifier) {
    Card(modifier = modifier.clickable(onClick = action), colors = CardDefaults.cardColors(containerColor = Color(0xFF10161A)), shape = RoundedCornerShape(14.dp)) {
        Column(Modifier.padding(16.dp)) {
            Text(code, color = VanguardColors.Lime, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
            Spacer(Modifier.height(7.dp))
            Text(title, fontWeight = FontWeight.Bold)
            Text(body, color = VanguardColors.Muted, fontSize = 12.sp)
        }
    }
}

@Composable
private fun InfoBanner(title: String, body: String, accent: Color) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF10161A))
            .border(1.dp, accent.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
            .padding(14.dp),
    ) {
        Box(Modifier.width(4.dp).height(40.dp).clip(CircleShape).background(accent))
        Spacer(Modifier.width(12.dp))
        Column {
            Text(title, color = accent, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            Text(body, color = VanguardColors.Muted, fontSize = 13.sp)
        }
    }
}

@Composable
private fun StatusPill(text: String, color: Color) {
    Box(Modifier.clip(RoundedCornerShape(50)).background(color.copy(alpha = 0.12f)).border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(50)).padding(horizontal = 10.dp, vertical = 7.dp)) {
        Text(text, color = color, fontFamily = FontFamily.Monospace, fontSize = 9.sp)
    }
}

@Composable
private fun SettingsCard(title: String, modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = Color(0xFF10161A)), shape = RoundedCornerShape(14.dp)) {
        Column(Modifier.padding(16.dp)) {
            Text(title, color = VanguardColors.Lime, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun ToggleRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, Modifier.weight(1f), fontSize = 13.sp)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun DriverRow(name: String, detail: String, state: String, color: Color) {
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color(0xFF10161A)).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(name, fontWeight = FontWeight.Bold)
            Text(detail, color = VanguardColors.Muted, fontSize = 12.sp)
        }
        StatusPill(state, color)
    }
}

private fun launchGame(context: android.content.Context, mode: EngineMode, mission: String? = null, server: String? = null) {
    val root = File(context.getExternalFilesDir(null), "game-data")
    val settings = SettingsRepository(context)
    val graphics: GraphicsProfile = settings.graphics()
    val request = LaunchRequest(
        mode = mode,
        assetRoot = root.absolutePath,
        renderer = graphics.renderer,
        performanceProfile = graphics.performance,
        renderScale = graphics.renderScale,
        refreshRateHz = graphics.targetRefreshHz,
        mission = mission,
        server = server,
    )
    context.startActivity(Intent(context, GameActivity::class.java).putExtra(GameActivity.EXTRA_REQUEST, Json.encodeToString(request)))
}
