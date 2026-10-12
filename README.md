# Freecam Lite 1.0.0 (Fabric, Minecraft 26.3, client-side)

Detach the camera and fly around while your character stays put.

- **F4** toggles freecam (rebind under Options > Controls > Freecam Lite).
- Move with your normal movement keys, Jump = up, Sneak = down, hold Sprint to go faster.
- Your character ignores movement keys while freecam is on. Attacking, breaking/placing blocks and interacting are blocked.
- Freecam turns off by itself on respawn, world change or disconnect.
- Only chunks and entities the server has already sent you are visible, so you cannot see farther than your render distance.
- Some servers do not allow freecam. Check the rules before using it online.

Build: Java 25, then `gradle build` (Gradle 9.6.0). Jar: `build/libs/freecamlite-1.0.0.jar`.
Needs Fabric Loader 0.19.5+ and Fabric API 0.161.0+26.3.
