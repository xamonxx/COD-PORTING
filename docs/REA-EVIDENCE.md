# Reference evidence and clean-room record

Analysis type: static only. The reference game was not executed by REA.

- Reference APK SHA-256:
  `a3b27a738b94dfddfa9e71ea43838946dce3a7bed9362f1f113b94f07935622c`
- REA evidence identifier:
  `ev_9d3fa3f6142f495cf8d0ae2965c8753d36642b09cbc0b0d03b2cbc342d348a4d`
- Observed manifest label/version: `COD4 Android Launcher` / `0.5.9`
- Observed platform range: minSdk 24, targetSdk 36
- High-level behaviors observed: game-folder import, campaign selection, private
  server connection, touch editor, gamepad input, graphics/performance controls,
  renderer selection, and isolated driver probing.

The reference APK and 6.8 GB Windows game distribution remain outside version
control. No extraction output, source-like decompilation, native library, string,
icon, layout, or asset from either input is present in this repository. UI text,
layout, and graphics were independently authored.

These observations establish requirements only. They do not prove runtime
behavior, compatibility, performance, or completeness.
