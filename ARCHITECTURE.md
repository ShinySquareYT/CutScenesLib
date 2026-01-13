# CutScenesLib Architecture Design

## Overview
CutScenesLib is a NeoForge 1.21.1 library mod that provides a complete API for creating, registering, and playing pausable 3D cutscenes in Minecraft.

## Package Structure

```
com.shinysquare.cutsceneslib/
├── api/                          # Public API for other mods
│   ├── CutsceneAPI.java         # Main entry point
│   ├── ICutscene.java           # Cutscene interface
│   ├── ICutscenePlayer.java     # Player interface
│   ├── ICutsceneTrigger.java    # Trigger interface
│   └── events/                  # Custom events
│       ├── CutsceneStartEvent.java
│       └── CutsceneEndEvent.java
├── core/                         # Internal implementation
│   ├── CutScenesLib.java        # Main mod class
│   ├── CutsceneManager.java     # Registration & lifecycle
│   ├── CutscenePlayer.java      # Playback engine
│   └── GamePauseHandler.java    # Pause mechanics
├── cutscene/                     # Cutscene data structures
│   ├── Cutscene.java            # Implementation
│   ├── CutsceneFrame.java       # Single frame data
│   ├── CameraPath.java          # Camera movement
│   └── SkinMapping.java         # Player skin zones
├── loader/                       # File loading
│   ├── CutsceneLoader.java      # JSON + binary loader
│   └── CutsceneValidator.java   # Validation
├── render/                       # Rendering
│   ├── CutsceneRenderer.java    # Main renderer
│   └── SkinTextureMapper.java   # Skin integration
└── trigger/                      # Event triggers
    ├── ItemTrigger.java         # Item pickup
    ├── AdvancementTrigger.java  # Achievement
    └── CustomTrigger.java       # Generic trigger
```

## File Format Specification

### Cutscene Metadata (JSON)
**Location:** `assets/<modid>/cutscenes/<name>/cutscene.json`

```json
{
  "name": "intro_cutscene",
  "duration": 10.0,
  "pauseGame": true,
  "animation": "intro.csanim",
  "camera": {
    "type": "path",
    "keyframes": [
      {"time": 0.0, "pos": [0, 5, 10], "rot": [0, 0, 0]},
      {"time": 5.0, "pos": [5, 5, 5], "rot": [0, 45, 0]},
      {"time": 10.0, "pos": [10, 5, 0], "rot": [0, 90, 0]}
    ]
  },
  "skinMapping": {
    "enabled": true,
    "zones": [
      {"name": "player_face", "uvStart": [0.0, 0.0], "uvEnd": [0.25, 0.25]}
    ]
  }
}
```

### Animation Data (.csanim)
**Binary format** containing:
- Header (magic bytes, version)
- Model data (vertices, UVs, normals)
- Keyframe transforms (position, rotation, scale per frame)
- Texture references

## API Usage Example

```java
// In another mod's initialization
public class MyMod {
    public void init() {
        // Register cutscene
        Cutscene cutscene = CutsceneAPI.loadCutscene(
            new ResourceLocation("mymod", "intro_cutscene")
        );
        
        // Trigger on item pickup
        CutsceneAPI.registerTrigger(
            new ItemTrigger(Items.DIAMOND),
            cutscene
        );
        
        // Or play manually
        CutsceneAPI.playCutscene(player, cutscene);
    }
}
```

## Game Pause Mechanism

When cutscene plays:
1. Store current game tick state
2. Set `Minecraft.pause = true` (even in multiplayer for client)
3. Disable entity ticking via event cancellation
4. Render cutscene overlay
5. Restore state on completion

## Player Skin Integration

1. Fetch player's skin texture from `PlayerInfo`
2. Extract specified UV regions from skin
3. Map to cutscene model textures in real-time
4. Cache per-player for performance

## Build Configuration

Other mods add dependency in `build.gradle`:
```gradle
repositories {
    maven { url 'https://maven.pkg.github.com/ShinySquareYT/CutScenesLib' }
}

dependencies {
    implementation fg.deobf("com.shinysquare:cutsceneslib:1.0.0")
}
```
