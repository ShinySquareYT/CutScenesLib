# CutScenesLib - GeckoLib Edition

A Minecraft NeoForge 1.21.1 library mod for creating, registering, and playing high-fidelity 3D cutscenes using **GeckoLib**.

## Features

- **GeckoLib Integration**: Uses GeckoLib for advanced model loading and smooth animation playback.
- **Blockbench Camera Support**: Animate the camera directly in Blockbench using the `camera` bone.
- **Dynamic Bone-Based Texture Mapping**: Easily swap textures on any bone (cube) at runtime, perfect for player skin integration.
- **Scene Composition**: Combine multiple animated models into one cutscene.
- **Full Game Pause**: Optionally pauses all game ticks (entities, world, etc.) during playback.
- **Event-Driven Triggers**: Automatically play cutscenes on item pickups or advancement completions.

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

#### Dynamic Bone Texture Mapping (Universal UV Mapping)

This is the new feature that allows you to change the texture of any part of your model at runtime.

```java
import net.shinysquare.cslib.api.CutsceneAPI;
import net.minecraft.resources.ResourceLocation;

public class MyModCode {
    public void setupCustomTextures() {
        // Example: Change the texture of a bone named "test" to a custom texture
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
  ]
}
```

### 4. Player Skin Integration

To use the player's skin:
1.  In your code, call `CutsceneAPI.mapTextureToBone("bone_name", CutsceneAPI.getPlayerSkinTexture())`.
2.  The `BoneTextureManager` will handle the texture swap during rendering.

---

## 🎓 **Confirmation: Blockbench Camera & UV Mapping**

**Yes, both are fully supported!**

| Feature | Implementation | How to Use |
|---|---|---|
| **Blockbench Camera** | The renderer looks for a bone named **`camera`** in your GeckoLib model and attaches the player's view to it. | Animate a bone named `camera` in Blockbench. |
| **Universal UV Mapping** | Implemented `BoneTextureManager` and `CutsceneAPI.mapTextureToBone()`. | Name a cube/bone (e.g., `"test"`) and use the API to map a texture to it. |

The library is now a powerful, GeckoLib-powered cinematic tool!
