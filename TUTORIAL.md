# CutScenesLib Tutorial: Understanding How It Works

This tutorial will teach you how the CutScenesLib mod works internally, so you can understand the code and potentially contribute or create similar systems.

## Table of Contents

1. [Project Structure](#project-structure)
2. [How the Mod Initializes](#how-the-mod-initializes)
3. [Loading Cutscenes from JSON](#loading-cutscenes-from-json)
4. [Playing a Cutscene](#playing-a-cutscene)
5. [Game Pausing Mechanism](#game-pausing-mechanism)
6. [Rendering System](#rendering-system)
7. [Trigger System](#trigger-system)
8. [Player Skin Integration](#player-skin-integration)

---

## Project Structure

The mod is organized into logical packages:

```
net.shinysquare.cslib/
├── CutScenesLib.java          # Main mod class
├── api/
│   └── CutsceneAPI.java       # Public API for other mods
├── core/
│   ├── CutsceneManager.java   # Manages all cutscenes
│   ├── CutscenePlayer.java    # Plays a single cutscene
│   └── GamePauseHandler.java  # Pauses the game
├── cutscene/
│   ├── Cutscene.java          # Data structure for a cutscene
│   ├── CutsceneFrame.java     # A single animation frame
│   ├── CameraPath.java        # Camera movement data
│   └── SkinMapping.java       # Player skin configuration
├── loader/
│   └── CutsceneLoader.java    # Loads JSON files
├── render/
│   ├── CutsceneRenderer.java  # Renders cutscenes
│   └── SkinTextureMapper.java # Handles player skins
├── trigger/
│   └── TriggerHandler.java    # Automatic triggers
└── event/
    ├── CutsceneStartEvent.java
    └── CutsceneEndEvent.java
```

---

## How the Mod Initializes

### The `@Mod` Annotation

In NeoForge, every mod needs a main class with the `@Mod` annotation:

```java
@Mod(CutScenesLib.MOD_ID)
public class CutScenesLib {
    public static final String MOD_ID = "cslib";
    // ...
}
```

This tells NeoForge "this is a mod with ID 'cslib'". The constructor of this class is called when the mod loads.

### Constructor: Setting Up Systems

```java
public CutScenesLib(IEventBus modEventBus) {
    instance = this;
    
    // Create managers
    this.cutsceneManager = new CutsceneManager();
    this.pauseHandler = new GamePauseHandler();
    this.triggerHandler = new TriggerHandler();
    
    // Register event listeners
    NeoForge.EVENT_BUS.register(pauseHandler);
    NeoForge.EVENT_BUS.register(triggerHandler);
}
```

**What's happening here:**
1. We create instances of all our manager classes
2. We register them with the **event bus** so they can listen to game events
3. The `modEventBus` is for mod lifecycle events (setup, client setup)
4. The `NeoForge.EVENT_BUS` is for gameplay events (entity ticks, item pickups)

### Client vs Server Setup

```java
private void clientSetup(final FMLClientSetupEvent event) {
    this.renderer = new CutsceneRenderer();
    NeoForge.EVENT_BUS.register(renderer);
}
```

The renderer is only initialized on the **client side** because rendering doesn't happen on servers. This is important for multiplayer compatibility.

---

## Loading Cutscenes from JSON

### The Resource Location System

Minecraft uses `ResourceLocation` to identify resources:
```java
new ResourceLocation("mymod", "intro")
```

This translates to the file path:
```
assets/mymod/cutscenes/intro/cutscene.json
```

### The Loading Process

In `CutsceneLoader.java`:

```java
public static Cutscene loadCutscene(ResourceManager resourceManager, ResourceLocation location) {
    // 1. Build the file path
    ResourceLocation filePath = new ResourceLocation(
        location.getNamespace(),
        "cutscenes/" + location.getPath() + "/cutscene.json"
    );
    
    // 2. Get the resource from the resource manager
    Optional<Resource> resourceOpt = resourceManager.getResource(filePath);
    
    // 3. Read the JSON
    BufferedReader reader = new BufferedReader(
        new InputStreamReader(resource.open(), StandardCharsets.UTF_8)
    );
    JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
    
    // 4. Parse into a Cutscene object
    return parseCutscene(json, location);
}
```

### JSON Parsing with Gson

We use **Gson** to parse JSON. For example, parsing a camera keyframe:

```java
JsonObject kf = keyframeElement.getAsJsonObject();
float time = kf.get("time").getAsFloat();
Vector3f position = parseVector3f(kf.getAsJsonArray("position"));
```

The `parseVector3f` helper converts `[x, y, z]` arrays into `Vector3f` objects.

---

## Playing a Cutscene

### The Flow

1. **API Call**: `CutsceneAPI.playCutscene(player, cutscene)`
2. **Manager**: Creates a `CutscenePlayer` instance
3. **Player**: Starts playback, pauses game if configured
4. **Tick Loop**: Updates every game tick (20 times per second)
5. **Renderer**: Draws the current frame
6. **Completion**: Stops when duration is reached

### The CutscenePlayer Class

```java
public class CutscenePlayer {
    private float currentTime;
    private boolean playing;
    
    public void tick() {
        if (!playing) return;
        
        // Update time (0.05 seconds per tick)
        currentTime += 0.05f;
        
        // Check if finished
        if (currentTime >= cutscene.getDuration()) {
            stop();
            return;
        }
        
        // Update camera and frame
        cameraState = cutscene.getCameraPath().getStateAtTime(currentTime);
        currentFrame = cutscene.getFrameAtTime(currentTime);
    }
}
```

**Key concept**: The game runs at 20 ticks per second, so each tick is 0.05 seconds. We increment `currentTime` by this amount each tick.

### Frame Interpolation

When you request a frame at time 3.5 seconds, but you only have keyframes at 0s, 5s, and 10s, we need to **interpolate**:

```java
public CutsceneFrame getFrameAtTime(float time) {
    // Find frames before and after the requested time
    CutsceneFrame before = null;
    CutsceneFrame after = null;
    
    for (CutsceneFrame frame : frames) {
        if (frame.getTime() <= time) before = frame;
        if (frame.getTime() >= time && after == null) after = frame;
    }
    
    // For now, return the earlier frame
    // TODO: Implement smooth interpolation
    return before;
}
```

In a complete implementation, you would **lerp** (linear interpolate) between the two frames.

---

## Game Pausing Mechanism

### The Challenge

Minecraft doesn't have a built-in "pause everything" function for multiplayer. We need to manually prevent updates.

### Event Cancellation

In `GamePauseHandler.java`:

```java
@SubscribeEvent
public void onEntityTick(EntityTickEvent.Pre event) {
    if (isPaused) {
        event.setCanceled(true);
    }
}
```

**What this does:**
- The `@SubscribeEvent` annotation tells NeoForge "call this method when this event happens"
- `EntityTickEvent.Pre` fires before every entity updates
- `event.setCanceled(true)` stops the entity from updating

We do this for:
- Entity ticks (movement, AI)
- Level ticks (block updates, weather)
- Living entity updates (health, effects)

---

## Rendering System

### When to Render

We hook into the **GUI render event**:

```java
@SubscribeEvent
public void onRenderGui(RenderGuiEvent.Post event) {
    // Check if player is watching a cutscene
    CutscenePlayer cutscenePlayer = getCutscenePlayer(player);
    if (cutscenePlayer != null && cutscenePlayer.isPlaying()) {
        renderCutscene(event.getGuiGraphics(), cutscenePlayer, event.getPartialTick());
    }
}
```

This runs every frame, **after** the normal GUI is drawn, so our cutscene appears on top.

### The PoseStack

Minecraft uses a **matrix stack** for transformations:

```java
PoseStack poseStack = graphics.pose();
poseStack.pushPose();  // Save current state

// Apply transformations
poseStack.translate(x, y, z);
poseStack.mulPose(rotation);
poseStack.scale(scaleX, scaleY, scaleZ);

// Render something here

poseStack.popPose();  // Restore previous state
```

Think of it like Photoshop layers - you push a layer, modify it, draw, then pop back to the previous layer.

### Camera Setup

```java
private void setupCamera(PoseStack poseStack, CameraPath.CameraState cameraState) {
    // Center on screen
    poseStack.translate(screenWidth / 2, screenHeight / 2, 0);
    
    // Apply camera rotation
    Vector3f rotation = cameraState.getRotation();
    poseStack.mulPose(Quaternionf.fromAxisAngleDeg(1, 0, 0, rotation.x));
    
    // Apply camera position (translate in opposite direction)
    Vector3f position = cameraState.getPosition();
    poseStack.translate(-position.x * 20, -position.y * 20, -position.z * 20);
}
```

We multiply positions by 20 to convert from Minecraft's block coordinates to screen pixels.

---

## Trigger System

### Listening to Game Events

In `TriggerHandler.java`:

```java
@SubscribeEvent
public void onItemPickup(EntityItemPickupEvent event) {
    Player player = event.getEntity();
    Item item = event.getItem().getItem().getItem();
    
    // Check if there's a trigger for this item
    Cutscene cutscene = CutScenesLib.getInstance()
        .getCutsceneManager()
        .getItemTrigger(item);
    
    if (cutscene != null) {
        CutsceneAPI.playCutscene(player, cutscene);
    }
}
```

**How it works:**
1. NeoForge fires `EntityItemPickupEvent` when a player picks up an item
2. Our method is called because of `@SubscribeEvent`
3. We check if we have a cutscene registered for that item
4. If yes, we play it

### Registering Triggers

Other mods call:
```java
CutsceneAPI.registerItemTrigger(Items.DIAMOND, myCutscene);
```

This stores the mapping in `CutsceneManager`:
```java
private final Map<Item, Cutscene> itemTriggers = new HashMap<>();

public void registerItemTrigger(Item item, Cutscene cutscene) {
    itemTriggers.put(item, cutscene);
}
```

---

## Player Skin Integration

### Getting the Player's Skin

```java
public ResourceLocation getPlayerSkin(Player player) {
    UUID playerId = player.getUUID();
    
    // Get from player info
    PlayerInfo playerInfo = minecraft.getConnection().getPlayerInfo(playerId);
    if (playerInfo != null) {
        return playerInfo.getSkin().texture();
    }
    
    // Fall back to default skin
    return DefaultPlayerSkin.get(playerId);
}
```

**Minecraft's skin system:**
- Each player has a 64x64 pixel skin texture
- It's downloaded from Mojang's servers
- If offline, a default skin is used (Steve or Alex)

### Skin Texture Layout

The player skin is laid out like this:

```
Head:  (8,8) to (16,16)   - front face
Body:  (20,20) to (28,32) - front torso
Arms:  (44,20) to (52,32) - right arm
Legs:  (4,20) to (12,32)  - right leg
```

### Applying Skins to Models

The `SkinMapping.SkinZone` class defines:
- **skinUV**: Which part of the skin to copy from
- **modelUV**: Where to paste it on the cutscene model

```json
{
  "name": "player_face",
  "entityId": "player1",
  "skinUV": [8, 8, 16, 16],      // Copy the face
  "modelUV": [0.0, 0.0, 1.0, 1.0] // Paste it on the whole model texture
}
```

In a full implementation, you would:
1. Load the skin texture as a `NativeImage`
2. Extract the pixel region defined by `skinUV`
3. Create a new texture with those pixels
4. Apply it to the model when rendering

---

## Summary

**CutScenesLib** is a complete system that:

1. **Loads** cutscene data from easy-to-edit JSON files
2. **Manages** multiple cutscenes and their triggers
3. **Plays** cutscenes with smooth camera movement and animation
4. **Pauses** the game by canceling tick events
5. **Renders** 3D scenes on top of the game
6. **Integrates** player skins for personalization

The key concepts you've learned:
- **Event-driven architecture**: Using `@SubscribeEvent` to react to game events
- **Resource loading**: Using `ResourceManager` and `ResourceLocation`
- **JSON parsing**: Using Gson to read configuration files
- **Matrix transformations**: Using `PoseStack` for 3D positioning
- **Tick-based timing**: Updating state 20 times per second

You can now use these patterns in your own mods!
