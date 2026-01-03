# CutScenesLib

A Minecraft NeoForge 1.21.1 library mod for creating, registering, and playing pausable 3D cutscenes.

## Features

- **Blockbench Integration**: Use standard Blockbench JSON models and animations.
- **Scene Composition**: Combine multiple models (e.g., ground, player, props) into one cutscene.
- **Advanced Skin Mapping**: Swap specific model textures (e.g., a texture named `player_skin`) with the player's actual skin.
- **External Camera Config**: Separate camera angles and positions into a dedicated `camera.json` file for easy tweaking.
- **Full Game Pause**: Optionally pauses all game ticks (entities, world, etc.) during playback.
- **Event-Driven Triggers**: Automatically play cutscenes on item pickups or advancement completions.

---

## For Developers: How to Use CutScenesLib

### 1. Add CutScenesLib to Your Project

(Instructions for `build.gradle` and `mods.toml` remain the same.)

### 2. Using the API

The `CutsceneAPI` class provides all the methods you need.

#### Loading and Playing a Cutscene

```java
import net.shinysquare.cslib.api.CutsceneAPI;
import net.shinysquare.cslib.cutscene.Cutscene;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class MyModCode {
    public void playMyCutscene(Player player) {
        // This looks for: assets/mymod/cutscenes/intro/cutscene.json
        ResourceLocation cutsceneId = new ResourceLocation("mymod", "intro");

        Cutscene introCutscene = CutsceneAPI.loadCutscene(cutsceneId);

        if (introCutscene != null) {
            CutsceneAPI.playCutscene(player, introCutscene);
        }
    }
}
```

---

## How to Create a Cutscene (Blockbench Workflow)

The new workflow involves creating your models and animations in **Blockbench** and defining the scene composition in a central `cutscene.json` file.

### 1. Blockbench Setup

1.  **Create your Model(s)**: Design your models (e.g., `player_prop.bbmodel`, `scene_ground.bbmodel`).
2.  **Create your Animation(s)**: Use the **Animate** tab in Blockbench to create animations for your models.
3.  **Export**: Export each model as a **Blockbench JSON Model** (e.g., `player_prop.json`, `scene_ground.json`).

### 2. File Structure

Place your files in your mod's resources folder:

```
src/main/resources/
└── assets/
    └── your_mod_id/
        └── cutscenes/
            └── my_awesome_cutscene/
                ├── cutscene.json           <-- Scene definition
                ├── camera.json             <-- Camera angles (Optional)
                ├── player_prop.json        <-- Blockbench Model 1
                ├── scene_ground.json       <-- Blockbench Model 2
                └── textures/
                    ├── player_prop.png
                    └── scene_ground.png
```

### 3. The `cutscene.json` File (Scene Composition)

The `cutscene.json` now defines the scene, linking to your Blockbench models and animations.

```json
{
  "name": "The First Diamond",
  "duration": 12.5,
  "pauseGame": true,
  
  // OPTION 1: External Camera Configuration
  "cameraConfig": "mymod:cutscenes/my_awesome_cutscene/camera.json",
  
  // OPTION 2: Inline Camera Configuration (if cameraConfig is not used)
  // "camera": { ... },
  
  "models": [
    {
      "id": "ground",
      "model": "mymod:cutscenes/my_awesome_cutscene/scene_ground.json",
      "texture": "mymod:textures/scene_ground.png",
      "position": [0, 0, 0],
      "rotation": [0, 0, 0],
      "scale": [1.0, 1.0, 1.0],
      "animation": "idle_animation" // Name of the animation in scene_ground.json
    },
    {
      "id": "player_character",
      "model": "mymod:cutscenes/my_awesome_cutscene/player_prop.json",
      "texture": "mymod:textures/player_prop.png",
      "position": [0, 0, 0],
      "rotation": [0, 180, 0],
      "scale": 1.0,
      "animation": "walk_animation", // Name of the animation in player_prop.json
      "usePlayerSkin": true,
      "skinTextureName": "player_skin" // Texture name in Blockbench to replace
    }
  ]
}
```

### 4. External Camera Configuration (`camera.json`)

This file is loaded separately and only contains the camera path.

**Location**: `assets/your_mod_id/cutscenes/my_awesome_cutscene/camera.json`

```json
{
  "type": "PATH",
  "keyframes": [
    { "time": 0.0, "position": [0, 70, 10], "rotation": [-20, 0, 0] },
    { "time": 5.0, "position": [5, 68, 5], "rotation": [-15, 45, 0] },
    { "time": 10.0, "position": [0, 65, 0], "rotation": [0, 90, 0] }
  ]
}
```

### 5. Advanced Player Skin Mapping

The skin mapping is now controlled by the `skinTextureName` field in the `models` array.

**How to set it up in Blockbench:**
1.  In Blockbench, go to the **Texture** tab.
2.  Ensure the texture you want to be replaced by the player's skin is named **`player_skin`** (or whatever you set `skinTextureName` to).
3.  Any part of your model that uses this texture will automatically be rendered with the current player's skin texture, while all other textures (like your ground texture) will remain unchanged.

---

## 🎓 **Confirmation: Blockbench Compatibility**

**Yes, you can now use Blockbench!**

The library is updated to use a **Scene Composition** approach, where you define your scene by listing the Blockbench models and the animation they should play. The Blockbench JSON format is now the primary way to define the 3D content and animation for your cutscenes.

Happy modding!
