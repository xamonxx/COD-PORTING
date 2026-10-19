# Architecture

## Boundary

The Android launcher owns configuration, SAF imports, profiles, diagnostics, and
process recovery. Native targets consume a validated `LaunchRequest`; they never
receive raw console text or an untrusted document URI.

```text
Compose launcher
  ├─ SAF importer ──> app-specific game-data/ + asset-manifest.json
  ├─ profile store ─> control/graphics schemas
  ├─ NativeBridge ──> libvanguard_sp.so
  │                   └─ future shared KisakCOD platform layer
  └─ NativeBridge ──> libvanguard_mp.so
                      └─ future shared KisakCOD platform layer
```

`NativeBridge` exposes initialize, start, pause, resume, input, stop, and an event
stream. JNI is currently a transparent non-playable stub so integration progress
cannot be confused with a functioning engine.

## Data contracts

Schemas under `app/src/main/assets/schemas/` are version 1 and reject unknown
fields. Migrations must be idempotent and preserve user-created control profiles.
Catalog updates must be HTTPS, size-bounded, Ed25519 verified, and atomically
published only after verification. Driver import is deliberately disabled until
that chain and isolated Vulkan presentation probing exist.

## Performance state machine

Adaptive mode tries the selected high-refresh target, lowers internal resolution
when the frame budget is missed, then steps to 90 and 60 Hz. Severe/critical
thermal state overrides performance preference. Renderer crash recovery records
the failed backend and retries GLES on the next safe launch; this persistence and
actual renderer switching remain future engine work.

## Port milestones

1. Compile census and Windows dependency isolation for pinned KisakCOD.
2. Shared AArch64 filesystem, thread, clock, networking, and command services.
3. SDL3/GameActivity lifecycle, audio, controller, and touch bridge.
4. Vulkan primary renderer, GLES recovery, pacing and performance hints.
5. Campaign vertical slice: first mission, input, audio, save/load.
6. Multiplayer vertical slice: direct connect, snapshot/input loop, clean exit.
7. Full mission and multiplayer compatibility, profiling, and device matrix.
