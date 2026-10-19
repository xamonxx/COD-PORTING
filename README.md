# Vanguard Mobile

Clean-room Android ARM64 launcher and porting foundation for a GPL game engine.

> **Current status: foundation build, not playable.** The Compose launcher,
> legal-data importer, profiles, safety validation, JNI contract, and two ARM64
> native targets build as an APK. KisakCOD gameplay, renderer, audio, save/load,
> and network runtime have not yet been ported or integrated.

## Clean-room boundary

This repository contains no code, DEX, native libraries, art, text, icons, or
game data copied from the reference APK. Static observations inform only public
behavioral requirements. Users must supply data they are legally entitled to
use through Android's Storage Access Framework. Commercial game archives,
Windows executables, Bink/Miles binaries, and vendor driver payloads are banned
from source and CI artifacts.

The future gameplay baseline is the GPL-3.0 project
[KisakCOD](https://github.com/SwagSoftware/KisakCOD), pinned in
`engine/kisakcod.lock`. SDL3 is independently pinned in
`engine/third-party.lock`. Neither is vendored or compiled in the current build.

## Implemented

- Original landscape Compose UI: command center, library/import, campaign,
  private-server connect, controls, performance, driver hub, diagnostics.
- Resumable app-private SAF import and a versioned asset manifest.
- Versioned control, graphics, driver catalog, and launch request contracts.
- Bounded server/argument validation and system-driver-first recovery design.
- Adaptive 120/90/60 Hz decision policy with thermal guardrails.
- `arm64-v8a` native targets `vanguard_sp` and `vanguard_mp` with JNI lifecycle,
  input, and event interfaces.
- Unit tests, static policy checks, debug/release GitHub Actions, signed tagged
  releases, checksum, SBOM, dependency notices, and corresponding-source bundle.

## Build

Requirements: JDK 17, Android SDK 36, NDK 28.2.13676358, CMake 3.31.6.

```bash
./gradlew lint testDebugUnitTest assembleDebug
```

The debug APK is written to `app/build/outputs/apk/debug/`. GitHub Actions installs
the pinned toolchain and runs the same checks. Only `arm64-v8a` is packaged.

## Signed release

Configure these repository secrets:

- `ANDROID_KEYSTORE_BASE64`
- `ANDROID_KEYSTORE_PASSWORD`
- `ANDROID_KEY_ALIAS`
- `ANDROID_KEY_PASSWORD`

Pushing a `v*` tag runs the release workflow. It verifies the signature and ABI,
then publishes `Vanguard-Mobile-arm64-release.apk`, its SHA-256 file, an SBOM,
dependency notices, and a GPL corresponding-source archive. Mapping symbols stay
in a restricted workflow artifact rather than the public release bundle.

## Roadmap to playable

The actual port remains substantial: isolate shared KisakCOD platform services,
make SP/MP compile for AArch64, replace Windows APIs, add SDL3/GameActivity,
implement Vulkan/GLES render backends, audio, input, networking, saves, then pass
campaign and private-server vertical slices on physical hardware. A build passing
CI is not evidence of gameplay, thermals, or driver compatibility.

See [Architecture](docs/ARCHITECTURE.md), [REA evidence boundary](docs/REA-EVIDENCE.md),
and [Contributing](CONTRIBUTING.md).
