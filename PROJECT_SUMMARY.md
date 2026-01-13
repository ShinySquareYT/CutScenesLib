# CutScenesLib - Project Summary

## Overview

**CutScenesLib** is a complete NeoForge 1.21.1 library mod that enables other Minecraft mods to create, register, and play pausable 3D cutscenes. The mod provides a clean API, JSON-based cutscene format, full game pause functionality, and player skin integration.

## Project Information

- **Mod ID**: `cslib`
- **Package**: `net.shinysquare.cslib`
- **Version**: 1.0.0
- **Minecraft Version**: 1.21.1
- **Mod Loader**: NeoForge 21.1.72
- **License**: MIT

## Key Features

### 1. Easy-to-Use API
- Static `CutsceneAPI` class for all interactions
- Simple method calls: `loadCutscene()`, `playCutscene()`, `registerTrigger()`
- No complex setup required for other mods

### 2. JSON-Based Cutscene Format
- Human-readable and easy to edit
- Supports camera paths with keyframes
- Defines entities, positions, rotations, and scales
- Configurable skin mapping zones

### 3. Full Game Pause
- Pauses all entity ticking (movement, AI, physics)
- Pauses world ticking (block updates, weather, time)
- Configurable per-cutscene
- Works in both singleplayer and multiplayer (client-side)

### 4. Event-Driven Triggers
- **Item Pickup**: Play cutscene when player picks up specific items
- **Advancement Completion**: Play cutscene when player earns achievements
- **Custom Events**: Other mods can fire custom triggers

### 5. Player Skin Integration
- Fetches player's actual Minecraft skin
- Maps skin regions onto cutscene models
- Configurable UV mapping zones
- Cached for performance

### 6. Customizable Camera Paths
- Smooth camera movement with keyframe interpolation
- Support for PATH, FIXED, and FOLLOW camera types
- Position and rotation control

## File Structure

```
CutScenesLib/
├── src/main/java/net/shinysquare/cslib/
│   ├── CutScenesLib.java              # Main mod class
│   ├── api/
│   │   └── CutsceneAPI.java           # Public API
│   ├── core/
│   │   ├── CutsceneManager.java       # Manages cutscenes
│   │   ├── CutscenePlayer.java        # Plays cutscenes
│   │   └── GamePauseHandler.java      # Pauses game
│   ├── cutscene/
│   │   ├── Cutscene.java              # Cutscene data
│   │   ├── CutsceneFrame.java         # Animation frames
│   │   ├── CameraPath.java            # Camera movement
│   │   └── SkinMapping.java           # Skin configuration
│   ├── loader/
│   │   └── CutsceneLoader.java        # JSON loader
│   ├── render/
│   │   ├── CutsceneRenderer.java      # Rendering system
│   │   └── SkinTextureMapper.java     # Skin integration
│   ├── trigger/
│   │   └── TriggerHandler.java        # Event triggers
│   └── event/
│       ├── CutsceneStartEvent.java    # Fired when cutscene starts
│       └── CutsceneEndEvent.java      # Fired when cutscene ends
├── src/main/resources/
│   └── META-INF/
│       └── mods.toml                   # Mod metadata
├── build.gradle                        # Build configuration
├── gradle.properties                   # Version properties
├── settings.gradle                     # Gradle settings
├── README.md                           # User documentation
├── TUTORIAL.md                         # Developer tutorial
├── ARCHITECTURE.md                     # Architecture design
└── example_cutscene.json               # Example cutscene file
```

## How to Use (For Mod Developers)

### 1. Add Dependency

In `build.gradle`:
```gradle
dependencies {
    implementation fg.deobf("net.shinysquare.cslib:cslib:1.0.0")
}
```

### 2. Create Cutscene JSON

Place in `assets/yourmod/cutscenes/intro/cutscene.json`:
```json
{
  "name": "Intro Cutscene",
  "duration": 10.0,
  "pauseGame": true,
  "camera": {
    "type": "PATH",
    "keyframes": [...]
  },
  "frames": [...]
}
```

### 3. Load and Play

```java
import net.shinysquare.cslib.api.CutsceneAPI;

// Load cutscene
Cutscene cutscene = CutsceneAPI.loadCutscene(
    new ResourceLocation("yourmod", "intro")
);

// Play for player
CutsceneAPI.playCutscene(player, cutscene);
```

### 4. Register Triggers

```java
// Play on item pickup
CutsceneAPI.registerItemTrigger(Items.DIAMOND, cutscene);

// Play on advancement
CutsceneAPI.registerAdvancementTrigger(
    new ResourceLocation("minecraft", "story/mine_diamond"),
    cutscene
);
```

## Technical Implementation

### Architecture Patterns

1. **Singleton Pattern**: Main mod class provides global access
2. **Manager Pattern**: Separate managers for different concerns
3. **Event-Driven**: Uses NeoForge event bus for triggers
4. **Data-Driven**: Cutscenes defined in JSON, not code

### Key Technologies

- **Gson**: JSON parsing
- **NeoForge Event Bus**: Event handling
- **PoseStack**: 3D transformations
- **ResourceManager**: Asset loading
- **PlayerInfo**: Skin texture access

### Game Pause Mechanism

The mod pauses the game by canceling tick events:
- `EntityTickEvent.Pre` - Stops entity updates
- `LevelTickEvent.Pre` - Stops world updates
- `LivingEvent.LivingTickEvent` - Extra layer for living entities

### Rendering Pipeline

1. Hook into `RenderGuiEvent.Post`
2. Check if player is watching cutscene
3. Fill screen with black background
4. Set up camera transformations
5. Render each entity in current frame
6. Apply player skin if configured
7. Draw progress bar

## Cutscene JSON Format

### Basic Structure

```json
{
  "name": "Display Name",
  "duration": 10.0,
  "pauseGame": true,
  "camera": { ... },
  "frames": [ ... ],
  "skinMapping": { ... }
}
```

### Camera Configuration

```json
"camera": {
  "type": "PATH",
  "keyframes": [
    {
      "time": 0.0,
      "position": [x, y, z],
      "rotation": [pitch, yaw, roll]
    }
  ]
}
```

### Frame Definition

```json
"frames": [
  {
    "time": 0.0,
    "entities": [
      {
        "id": "player1",
        "model": "player",
        "position": [x, y, z],
        "rotation": [pitch, yaw, roll],
        "scale": 1.0,
        "usePlayerSkin": true
      }
    ]
  }
]
```

### Skin Mapping

```json
"skinMapping": {
  "enabled": true,
  "zones": [
    {
      "name": "player_face",
      "entityId": "player1",
      "skinUV": [8, 8, 16, 16],
      "modelUV": [0.0, 0.0, 1.0, 1.0]
    }
  ]
}
```

## Future Enhancements

Potential improvements for future versions:

1. **3D Model Loading**: Support for custom 3D models (OBJ, glTF)
2. **Advanced Interpolation**: Smooth interpolation between keyframes
3. **Sound Integration**: Play audio during cutscenes
4. **Particle Effects**: Add particle systems to cutscenes
5. **Scripting Support**: Lua or JavaScript for dynamic cutscenes
6. **GUI Editor**: Visual editor for creating cutscenes
7. **Multiplayer Sync**: Synchronize cutscenes across players
8. **Recording Tools**: Record gameplay as cutscenes

## Building the Mod

### Prerequisites
- Java 21 or higher
- Gradle (included via wrapper)

### Build Commands

```bash
# Build the mod
./gradlew build

# Run client for testing
./gradlew runClient

# Run server for testing
./gradlew runServer

# Publish to Maven (requires credentials)
./gradlew publish
```

### Output

Built JAR file: `build/libs/cslib-1.0.0.jar`

## Documentation Files

- **README.md**: User-facing documentation and API reference
- **TUTORIAL.md**: In-depth tutorial on how the mod works internally
- **ARCHITECTURE.md**: Original architecture design document
- **example_cutscene.json**: Example cutscene file with all features

## Contributing

To contribute to CutScenesLib:

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## License

This project is licensed under the MIT License. See the LICENSE file for details.

## Credits

- **Author**: ShinySquare
- **Minecraft Version**: 1.21.1
- **Mod Loader**: NeoForge
- **Built with**: Java, Gradle, Gson

---

**Repository**: https://github.com/ShinySquareYT/CutScenesLib
**Version**: 1.0.0
**Last Updated**: January 2026
