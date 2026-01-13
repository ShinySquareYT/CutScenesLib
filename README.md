# CutScenesLib - GeckoLib Edition with Recording & Overlays

A Minecraft NeoForge 1.21.1 library mod for creating, registering, and playing high-fidelity 3D cutscenes using **GeckoLib**, now with **player recording** and **visual overlays**.

## Features

- **GeckoLib Integration**: Uses GeckoLib for advanced model loading and smooth animation playback.
- **Blockbench Camera Support**: Animate the camera directly in Blockbench using the `camera` bone.
- **Dynamic Bone-Based Texture Mapping**: Easily swap textures on any bone (cube) at runtime, perfect for player skin integration.
- **Scene Composition**: Combine multiple animated models into one cutscene.
- **Full Game Pause**: Optionally pauses all game ticks (entities, world, etc.) during playback.
- **Event-Driven Triggers**: Automatically play cutscenes on item pickups or advancement completions.
- **Player Recording System**: Record player movement and world snapshots to create dynamic cutscenes.
- **Visual Overlays**: Add solid color or transparent overlays to cutscenes for cinematic effects.

---

## For Developers: How to Use CutScenesLib

### 1. Add Dependencies

You must include both **CutScenesLib** and **GeckoLib** in your `build.gradle`:

```gradle
repositories {
    // ... other repositories
    maven {
        name = "GeckoLib"
        url = "https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/"
    }
}

dependencies {
    // ... other dependencies
    implementation "software.bernie.geckolib:geckolib-neoforge-1.21.1:4.6.1" // Latest GeckoLib version
    implementation fg.deobf("net.shinysquare.cslib:cslib:1.0.0") // CutScenesLib
}
```

You must also add the `geckolib` dependency to your `mods.toml`.

### 2. Using the API

The `CutsceneAPI` remains the primary interface.

#### Dynamic Bone Texture Mapping (Native GeckoLib UV Mapping)

This feature uses GeckoLib's native rendering layers to swap textures on specific bones, providing a highly efficient and flexible way to customize your models at runtime.

```java
import net.shinysquare.cslib.api.CutsceneAPI;
import net.minecraft.resources.ResourceLocation;

public class MyModCode {
    public void setupCustomTextures() {
        // Example: Change the texture of a bone named "test" to a custom texture
        // This mapping is global and applies to all cutscenes
        CutsceneAPI.mapTextureToBone("test", new ResourceLocation("mymod", "textures/special_glow.png"));
        
        // Example: Map the player's skin to a bone named "player_head"
        // The mod will automatically handle getting the player's current skin texture
        CutsceneAPI.mapTextureToBone("player_head", CutsceneAPI.getPlayerSkinTexture());
    }
}
```

---

## How to Create a Cutscene (GeckoLib Workflow)

The workflow now relies entirely on GeckoLib's file structure.

### 1. Blockbench Setup

1.  **Create your Model(s)**: Design your models.
2.  **Create your Animation(s)**: Use the **Animate** tab.
3.  **Camera Animation**: Add a bone named **`camera`** to your model. Animate this bone to define the camera's movement.
4.  **Export**: Export your model as a **GeckoLib Model** (e.g., `my_scene.geo.json`) and your animation as a **GeckoLib Animation** (e.g., `my_scene.animation.json`).

### 2. File Structure

Place your files in your mod's resources folder:

```
src/main/resources/
└── assets/
    └── your_mod_id/
        ├── geo/
        │   └── my_scene.geo.json           <-- GeckoLib Model
        ├── animations/
        │   └── my_scene.animation.json     <-- GeckoLib Animation
        └── textures/
            └── my_scene.png                <-- Model Texture
```

### 3. The `cutscene.json` (Scene Composition)

The `cutscene.json` now links to the GeckoLib files.

```json
{
  "name": "The First Diamond",
  "duration": 12.5,
  "pauseGame": true,
  
  // Camera is now controlled by the "camera" bone in the model, 
  // so no separate camera path is needed unless you want to override it.
  
  "models": [
    {
      "id": "main_scene",
      "geometry": "mymod:geo/my_scene.geo.json",
      "animation_file": "mymod:animations/my_scene.animation.json",
      "texture": "mymod:textures/my_scene.png",
      "animation": "scene_intro", // Name of the animation in my_scene.animation.json
      
      // Dynamic Bone Mappings: Map bone names to textures
      "bone_mappings": {
        "ground_bone": "mymod:textures/special_ground.png",
        "player_head": "cslib:textures/player_skin.png" // Use a placeholder texture that will be swapped by the API
      }
    }
  ],
  "overlay": {
    "color": "#000000", // Hex color code (e.g., #FF0000 for red)
    "opacity": 0.8      // 0.0 (transparent) to 1.0 (solid)
  },
  "recording": "mymod:recordings/my_recorded_scene.json" // Path to a recorded cutscene file
}
```

### 4. Player Skin Integration

To use the player's skin:
1.  In your code, call `CutsceneAPI.mapTextureToBone("bone_name", CutsceneAPI.getPlayerSkinTexture())`.
2.  The `BoneTextureManager` will handle the texture swap during rendering.

---

## Player Cutscenes: Recording & Playback

### 1. Recording In-Game

Use the `/cslib` command to record player movement and a world snapshot.

```
/cslib record <name> <radius>
```
- `<name>`: A unique name for your recorded cutscene (e.g., `my_intro_walk`).
- `<radius>`: The radius (in blocks) around the player to capture the world snapshot (e.g., `10`).

Example:
```
/cslib record my_intro_walk 15
```

To stop recording:
```
/cslib stop
```

This will save a JSON file (e.g., `my_intro_walk.json`) in the `cutscenes/recordings` folder within your Minecraft instance directory. This file contains the player's path and a snapshot of the blocks in the recorded area.

### 2. Playing Recorded Cutscenes

To play a recorded cutscene, reference its path in your `cutscene.json`:

```json
{
  "name": "My Recorded Walkthrough",
  "duration": 30.0, // Match the duration of your recording
  "pauseGame": true,
  "recording": "mymod:recordings/my_intro_walk.json" // Path to your recorded JSON
}
```

When this cutscene plays, the mod will:
- Recreate the recorded world snapshot.
- Play back the player's movement path.
- The player's view will follow the recorded path.

---

## Visual Overlays

You can add a visual overlay to any cutscene using the `overlay` field in your `cutscene.json`:

```json
"overlay": {
  "color": "#RRGGBB", // Hex color code (e.g., #FF0000 for red, #000000 for black)
  "opacity": 0.0 to 1.0 // Transparency, where 0.0 is fully transparent and 1.0 is solid
}
```

Example for a semi-transparent blue tint:
```json
"overlay": {
  "color": "#0000FF",
  "opacity": 0.3
}
```

---

## 🎓 **Confirmation: Blockbench Camera & Native UV Mapping**

**Yes, both are fully supported!**

| Feature | Implementation | How to Use |
|---|---|---|
| **Blockbench Camera** | The renderer looks for a bone named **`camera`** in your GeckoLib model and attaches the player's view to it. | Animate a bone named `camera` in Blockbench. |
| **Native UV Mapping** | Implemented `BoneTextureRenderLayer` and `CutsceneAPI.mapTextureToBone()`. | Name a cube/bone (e.g., `"test"`) and use the API to map a texture to it. |

The library is now a powerful, GeckoLib-powered cinematic tool with recording capabilities!
