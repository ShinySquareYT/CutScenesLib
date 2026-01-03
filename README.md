# CutScenesLib

A Minecraft NeoForge 1.21.1 library mod for creating, registering, and playing pausable 3D cutscenes.

**CutScenesLib** provides a complete API for other mods to add cinematic cutscenes to their content, triggered by in-game events like picking up items or earning advancements.

## Features

- **Easy-to-use API**: A simple, static `CutsceneAPI` class for all interactions.
- **JSON-based Cutscenes**: Define cutscenes with an easy-to-edit JSON file.
- **Full Game Pause**: Optionally pauses all game ticks (entities, world, etc.) during playback.
- **Event-Driven Triggers**: Automatically play cutscenes on item pickups or advancement completions.
- **Player Skin Integration**: Optionally map the player's skin onto elements within the cutscene for a personalized experience.
- **Customizable Camera Paths**: Define complex camera movements with keyframes.

---

## For Players: Installation

1.  Download the latest version of **CutScenesLib** from the [releases page](https://github.com/ShinySquareYT/CutScenesLib/releases).
2.  Make sure you have **NeoForge for Minecraft 1.21.1** installed.
3.  Place the downloaded `.jar` file into your `mods` folder.
4.  Also, install any other mods that require this library.

---

## For Developers: How to Use CutScenesLib

### 1. Add CutScenesLib to Your Project

In your `build.gradle` file, add the GitHub Packages repository and the dependency:

```gradle
repositories {
    // ... other repositories
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/ShinySquareYT/CutScenesLib")
        credentials {
            username = project.findProperty("gpr.user") ?: System.getenv("USERNAME")
            password = project.findProperty("gpr.key") ?: System.getenv("TOKEN")
        }
    }
}

dependencies {
    // ... other dependencies
    implementation fg.deobf("net.shinysquare.cslib:cslib:1.0.0") // Replace with the latest version
}
```

### 2. Add to `mods.toml`

Make sure your mod depends on CutScenesLib by adding this to your `src/main/resources/META-INF/mods.toml` file:

```toml
[[dependencies.yourmodid]] # Replace with your mod ID
modId="cslib"
type="required"
versionRange="[1.0.0,)" # Replace with the version you are using
ordering="NONE"
side="BOTH"
```

### 3. Using the API

The `CutsceneAPI` class provides all the methods you need. Here are some common use cases:

#### Loading and Playing a Cutscene

```java
import net.shinysquare.cslib.api.CutsceneAPI;
import net.shinysquare.cslib.cutscene.Cutscene;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class MyModCode {
    public void playMyCutscene(Player player) {
        // 1. Define the location of your cutscene
        // This looks for: assets/mymod/cutscenes/intro/cutscene.json
        ResourceLocation cutsceneId = new ResourceLocation("mymod", "intro");

        // 2. Load the cutscene
        // It's best to load this once and store it somewhere
        Cutscene introCutscene = CutsceneAPI.loadCutscene(cutsceneId);

        // 3. Play it for the player
        if (introCutscene != null) {
            CutsceneAPI.playCutscene(player, introCutscene);
        }
    }
}
```

#### Registering Triggers

You can automatically play a cutscene when something happens in the game.

**Item Pickup Trigger:**

```java
// In your mod's setup method
public void commonSetup() {
    ResourceLocation cutsceneId = new ResourceLocation("mymod", "found_diamond_sword");
    Cutscene swordCutscene = CutsceneAPI.loadCutscene(cutsceneId);

    if (swordCutscene != null) {
        // Play this cutscene whenever a player picks up a diamond sword
        CutsceneAPI.registerItemTrigger(Items.DIAMOND_SWORD, swordCutscene);
    }
}
```

**Advancement Completion Trigger:**

```java
// In your mod's setup method
public void commonSetup() {
    ResourceLocation cutsceneId = new ResourceLocation("mymod", "nether_welcome");
    Cutscene netherCutscene = CutsceneAPI.loadCutscene(cutsceneId);

    if (netherCutscene != null) {
        // Play this cutscene when the player gets the "We Need to Go Deeper" advancement
        ResourceLocation advancementId = new ResourceLocation("minecraft", "story/enter_the_nether");
        CutsceneAPI.registerAdvancementTrigger(advancementId, netherCutscene);
    }
}
```

---

## How to Create a Cutscene

Creating a cutscene involves making a `cutscene.json` file that defines everything about it.

### 1. Directory Structure

Your cutscene files must be placed in your mod's resources folder like this:

```
src/main/resources/
└── assets/
    └── your_mod_id/
        └── cutscenes/
            └── my_awesome_cutscene/
                └── cutscene.json
```

In this example, the `ResourceLocation` to load the cutscene would be `new ResourceLocation("your_mod_id", "my_awesome_cutscene")`.

### 2. The `cutscene.json` File

This file contains all the data for your cutscene. Here is a documented example:

```json
{
  "name": "The First Diamond",
  "duration": 12.5,
  "pauseGame": true,
  "camera": {
    "type": "PATH",
    "keyframes": [
      { "time": 0.0, "position": [0, 70, 10], "rotation": [-20, 0, 0] },
      { "time": 5.0, "position": [5, 68, 5], "rotation": [-15, 45, 0] },
      { "time": 10.0, "position": [0, 65, 0], "rotation": [0, 90, 0] },
      { "time": 12.5, "position": [0, 65, 0], "rotation": [0, 90, 0] }
    ]
  },
  "frames": [
    {
      "time": 0.0,
      "entities": [
        {
          "id": "player_character",
          "model": "player",
          "position": [0, 64, 0],
          "rotation": [0, 180, 0],
          "scale": 1.0,
          "usePlayerSkin": true
        },
        {
          "id": "sparkle_effect",
          "model": "cube",
          "position": [0, 65, 2],
          "rotation": [0, 0, 0],
          "scale": [0.1, 0.1, 0.1]
        }
      ]
    },
    {
      "time": 10.0,
      "entities": [
        {
          "id": "player_character",
          "model": "player",
          "position": [0, 64, 0],
          "rotation": [0, 270, 0], 
          "scale": 1.0,
          "usePlayerSkin": true
        }
      ]
    }
  ],
  "skinMapping": {
    "enabled": true,
    "zones": [
      {
        "name": "player_face",
        "entityId": "player_character",
        "skinUV": [8, 8, 16, 16],
        "modelUV": [0, 0, 1, 1]
      }
    ]
  }
}
```

### `cutscene.json` Field Reference

| Field | Type | Description |
|---|---|---|
| `name` | String | The display name of the cutscene. Not used internally. |
| `duration` | Number | Total duration of the cutscene in seconds. |
| `pauseGame` | Boolean | If `true`, the game world will be frozen during the cutscene. Defaults to `true`. |
| `camera` | Object | Defines the camera's movement. See below. |
| `frames` | Array | An array of keyframes that define the animation. See below. |
| `skinMapping` | Object | Configuration for applying the player's skin to models. See below. |

#### The `camera` Object

| Field | Type | Description |
|---|---|---|
| `type` | String | The type of camera movement. Can be `PATH`, `FIXED`, or `FOLLOW`. |
| `keyframes` | Array | An array of `CameraKeyframe` objects. |

A **`CameraKeyframe`** object has:
- `time` (Number): The time in seconds when this keyframe is active.
- `position` (Array): An array of 3 numbers `[x, y, z]` for the camera's position.
- `rotation` (Array): An array of 3 numbers `[pitch, yaw, roll]` for the camera's rotation in degrees.

#### The `frames` Array

This is an array of `CutsceneFrame` objects. Each `CutsceneFrame` has:
- `time` (Number): The time in seconds when this frame is active.
- `entities` (Array): An array of `FrameEntity` objects that are visible in this frame.

A **`FrameEntity`** object has:
- `id` (String): A unique identifier for this entity. This is crucial for interpolating movement between frames.
- `model` (String): The model to render. Can be `player`, `cube`, or a custom model ID (feature coming soon).
- `position`, `rotation`, `scale`: Transformations for this entity. `scale` can be a single number for uniform scaling or an array `[x, y, z]`.
- `usePlayerSkin` (Boolean): If `true`, the system will attempt to apply the player's skin to this model.

#### The `skinMapping` Object

| Field | Type | Description |
|---|---|---|
| `enabled` | Boolean | If `true`, skin mapping will be attempted. |
| `zones` | Array | An array of `SkinZone` objects. |

A **`SkinZone`** object defines how to map a part of the player's skin to a model:
- `name` (String): A descriptive name for the zone.
- `entityId` (String): The ID of the `FrameEntity` this zone applies to.
- `skinUV` (Array): `[u_start, v_start, u_end, v_end]` coordinates (in pixels) on the 64x64 player skin texture.
- `modelUV` (Array): `[u_start, v_start, u_end, v_end]` coordinates (from 0.0 to 1.0) on the target model's texture map.

---

Happy modding!
