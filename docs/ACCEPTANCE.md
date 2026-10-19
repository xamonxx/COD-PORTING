# Playable acceptance gate

A tagged release must not be described as playable until all checks below pass on
an iQOO 15R physical device.

## Campaign slice

- Import legally owned data through SAF.
- Load the first mission.
- Touch and gamepad input both affect gameplay.
- Audio is stable and save/load round-trips.

## Multiplayer slice

- Connect to an authorized private test server.
- Enter gameplay, send input, receive snapshots, and disconnect cleanly.

## Soak and performance

- 30 minutes without crash, ANR, or unbounded memory growth.
- Adaptive mode targets 120 FPS and changes to 90/60 without restart when frame
  budget or thermal limits require it.
- Vulkan failure recovers through GLES without a relaunch loop.

GitHub builds establish reproducibility only; they cannot satisfy this gate.
