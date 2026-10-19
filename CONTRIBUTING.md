# Contributing

All contributions must be original or compatible with GPL-3.0-only. Never submit
files extracted from the reference APK or proprietary game distributions.

Before opening a change:

1. Run `./gradlew lint testDebugUnitTest assembleDebug`.
2. Run `bash scripts/check-proprietary.sh .`.
3. Keep imported game data outside the repository.
4. Document the origin and license of every new third-party dependency.
5. Do not label a release playable until both physical-device vertical slices in
   `docs/ACCEPTANCE.md` pass.

Engine work should preserve the shared platform layer and keep campaign and
multiplayer compile targets independently testable.
