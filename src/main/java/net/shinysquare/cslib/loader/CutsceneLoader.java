package net.shinysquare.cslib.loader;

import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.shinysquare.cslib.CutScenesLib;
import net.shinysquare.cslib.cutscene.*;
import org.joml.Vector3f;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Loads cutscenes from JSON files
 * 
 * Cutscenes are stored at: assets/[namespace]/cutscenes/[path]/cutscene.json
 * 
 * Example JSON structure:
 * {
 *   "name": "Intro Cutscene",
 *   "duration": 10.0,
 *   "pauseGame": true,
 *   "camera": {
 *     "type": "path",
 *     "keyframes": [
 *       {"time": 0.0, "position": [0, 5, 10], "rotation": [0, 0, 0]},
 *       {"time": 5.0, "position": [5, 5, 5], "rotation": [0, 45, 0]}
 *     ]
 *   },
 *   "frames": [
 *     {
 *       "time": 0.0,
 *       "entities": [
 *         {
 *           "id": "player1",
 *           "model": "player",
 *           "position": [0, 0, 0],
 *           "rotation": [0, 0, 0],
 *           "scale": [1, 1, 1],
 *           "usePlayerSkin": true
 *         }
 *       ]
 *     }
 *   ],
 *   "skinMapping": {
 *     "enabled": true,
 *     "zones": []
 *   }
 * }
 * 
 * @author ShinySquare
 */
public class CutsceneLoader {
    
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    
    /**
     * Load a cutscene from a resource location
     * 
     * @param resourceManager The resource manager to load from
     * @param location The resource location (e.g., "mymod:intro")
     * @return The loaded cutscene, or null if loading failed
     */
    public static Cutscene loadCutscene(ResourceManager resourceManager, ResourceLocation location) {
        // Build the path: assets/[namespace]/cutscenes/[path]/cutscene.json
        ResourceLocation filePath = new ResourceLocation(
                location.getNamespace(),
                "cutscenes/" + location.getPath() + "/cutscene.json"
        );
        
        try {
            // Get the resource
            Optional<Resource> resourceOpt = resourceManager.getResource(filePath);
            
            if (resourceOpt.isEmpty()) {
                CutScenesLib.LOGGER.error("Cutscene file not found: {}", filePath);
                return null;
            }
            
            Resource resource = resourceOpt.get();
            
            // Read the JSON
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.open(), StandardCharsets.UTF_8)
            );
            
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            reader.close();
            
            // Parse the JSON into a Cutscene object
            Cutscene cutscene = parseCutscene(json, location);
            
            CutScenesLib.LOGGER.info("Successfully loaded cutscene: {}", location);
            return cutscene;
            
        } catch (Exception e) {
            CutScenesLib.LOGGER.error("Failed to load cutscene: {}", location, e);
            return null;
        }
    }
    
    /**
     * Parse a JSON object into a Cutscene
     */
    private static Cutscene parseCutscene(JsonObject json, ResourceLocation id) {
        Cutscene cutscene = new Cutscene();
        cutscene.setId(id);
        
        // Parse basic properties
        if (json.has("name")) {
            cutscene.setName(json.get("name").getAsString());
        }
        
        if (json.has("duration")) {
            cutscene.setDuration(json.get("duration").getAsFloat());
        }
        
        if (json.has("pauseGame")) {
            cutscene.setPauseGame(json.get("pauseGame").getAsBoolean());
        }
        
        // Overlay settings
        if (json.has("overlay")) {
            JsonObject overlay = json.getAsJsonObject("overlay");
            cutscene.setHasOverlay(true);
            if (overlay.has("color")) {
                cutscene.setOverlayColor(Integer.parseInt(overlay.get("color").getAsString().replace("#", ""), 16));
            }
            if (overlay.has("opacity")) {
                cutscene.setOverlayOpacity(overlay.get("opacity").getAsFloat());
            }
        }
        
        // Recording settings
        if (json.has("recording")) {
            cutscene.setRecording(true);
            cutscene.setRecordingPath(json.get("recording").getAsString());
        }
        
        // External Camera Config Support
        if (json.has("cameraConfig")) {
            cutscene.setCameraConfigLocation(new ResourceLocation(json.get("cameraConfig").getAsString()));
        } else if (json.has("camera")) {
            cutscene.setCameraPath(parseCameraPath(json.getAsJsonObject("camera")));
        }
        
        // Scene Composition: Load multiple Blockbench models
        if (json.has("models")) {
            JsonArray modelsArray = json.getAsJsonArray("models");
            for (JsonElement modelElement : modelsArray) {
                cutscene.addModel(parseSceneModel(modelElement.getAsJsonObject()));
            }
        }
        
        return cutscene;
    }

    private static GeckoSceneModel parseGeckoModel(JsonObject json) {
        String id = json.get("id").getAsString();
        ResourceLocation geo = new ResourceLocation(json.get("geometry").getAsString());
        ResourceLocation anim = new ResourceLocation(json.get("animation_file").getAsString());
        ResourceLocation tex = new ResourceLocation(json.get("texture").getAsString());
        
        GeckoSceneModel model = new GeckoSceneModel(id, geo, anim, tex);
        
        if (json.has("animation")) {
            model.setCurrentAnimation(json.get("animation").getAsString());
        }
        
        // Dynamic UV / Texture Mapping
        if (json.has("bone_mappings")) {
            JsonObject mappings = json.getAsJsonObject("bone_mappings");
            for (String boneName : mappings.keySet()) {
                model.mapTextureToBone(boneName, new ResourceLocation(mappings.get(boneName).getAsString()));
            }
        }
        
        return model;
    }
    
    /**
     * Load an external camera configuration
     */
    public static CameraPath loadCameraConfig(ResourceManager resourceManager, ResourceLocation location) {
        try {
            Optional<Resource> resourceOpt = resourceManager.getResource(location);
            if (resourceOpt.isEmpty()) return null;
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(resourceOpt.get().open(), StandardCharsets.UTF_8));
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            reader.close();
            
            return parseCameraPath(json);
        } catch (Exception e) {
            CutScenesLib.LOGGER.error("Failed to load camera config: {}", location, e);
            return null;
        }
    }

    /**
     * Parse camera path from JSON
     */
    private static CameraPath parseCameraPath(JsonObject json) {
        CameraPath path = new CameraPath();
        
        if (json.has("type")) {
            String typeStr = json.get("type").getAsString().toUpperCase();
            path.setType(CameraPath.CameraType.valueOf(typeStr));
        }
        
        if (json.has("keyframes")) {
            JsonArray keyframesArray = json.getAsJsonArray("keyframes");
            for (JsonElement keyframeElement : keyframesArray) {
                JsonObject kf = keyframeElement.getAsJsonObject();
                
                float time = kf.get("time").getAsFloat();
                Vector3f position = parseVector3f(kf.getAsJsonArray("position"));
                Vector3f rotation = parseVector3f(kf.getAsJsonArray("rotation"));
                
                path.addKeyframe(new CameraPath.CameraKeyframe(time, position, rotation));
            }
        }
        
        return path;
    }
    
    /**
     * Parse a frame from JSON
     */
    private static CutsceneFrame parseFrame(JsonObject json) {
        float time = json.get("time").getAsFloat();
        CutsceneFrame frame = new CutsceneFrame(time);
        
        if (json.has("entities")) {
            JsonArray entitiesArray = json.getAsJsonArray("entities");
            for (JsonElement entityElement : entitiesArray) {
                frame.addEntity(parseFrameEntity(entityElement.getAsJsonObject()));
            }
        }
        
        return frame;
    }
    
    /**
     * Parse a frame entity from JSON
     */
    private static CutsceneFrame.FrameEntity parseFrameEntity(JsonObject json) {
        CutsceneFrame.FrameEntity entity = new CutsceneFrame.FrameEntity();
        
        if (json.has("id")) {
            entity.setId(json.get("id").getAsString());
        }
        
        if (json.has("model")) {
            entity.setModel(json.get("model").getAsString());
        }
        
        if (json.has("position")) {
            entity.setPosition(parseVector3f(json.getAsJsonArray("position")));
        }
        
        if (json.has("rotation")) {
            entity.setRotation(parseVector3f(json.getAsJsonArray("rotation")));
        }
        
        if (json.has("scale")) {
            JsonElement scaleElement = json.get("scale");
            if (scaleElement.isJsonArray()) {
                entity.setScale(parseVector3f(scaleElement.getAsJsonArray()));
            } else {
                // Allow single number for uniform scale
                float scale = scaleElement.getAsFloat();
                entity.setScale(scale);
            }
        }
        
        if (json.has("usePlayerSkin")) {
            entity.setUsePlayerSkin(json.get("usePlayerSkin").getAsBoolean());
        }
        
        return entity;
    }
    
    /**
     * Parse skin mapping from JSON
     */
    private static SkinMapping parseSkinMapping(JsonObject json) {
        SkinMapping mapping = new SkinMapping();
        
        if (json.has("enabled")) {
            mapping.setEnabled(json.get("enabled").getAsBoolean());
        }
        
        if (json.has("zones")) {
            JsonArray zonesArray = json.getAsJsonArray("zones");
            for (JsonElement zoneElement : zonesArray) {
                mapping.addZone(parseSkinZone(zoneElement.getAsJsonObject()));
            }
        }
        
        return mapping;
    }
    
    /**
     * Parse a skin zone from JSON
     */
    private static SkinMapping.SkinZone parseSkinZone(JsonObject json) {
        SkinMapping.SkinZone zone = new SkinMapping.SkinZone();
        
        if (json.has("name")) {
            zone.setName(json.get("name").getAsString());
        }
        
        if (json.has("entityId")) {
            zone.setEntityId(json.get("entityId").getAsString());
        }
        
        if (json.has("skinUV")) {
            JsonArray skinUV = json.getAsJsonArray("skinUV");
            zone.setSkinUStart(skinUV.get(0).getAsFloat());
            zone.setSkinVStart(skinUV.get(1).getAsFloat());
            zone.setSkinUEnd(skinUV.get(2).getAsFloat());
            zone.setSkinVEnd(skinUV.get(3).getAsFloat());
        }
        
        if (json.has("modelUV")) {
            JsonArray modelUV = json.getAsJsonArray("modelUV");
            zone.setModelUStart(modelUV.get(0).getAsFloat());
            zone.setModelVStart(modelUV.get(1).getAsFloat());
            zone.setModelUEnd(modelUV.get(2).getAsFloat());
            zone.setModelVEnd(modelUV.get(3).getAsFloat());
        }
        
        return zone;
    }
    
    /**
     * Parse a Vector3f from a JSON array [x, y, z]
     */
    private static Vector3f parseVector3f(JsonArray array) {
        float x = array.get(0).getAsFloat();
        float y = array.get(1).getAsFloat();
        float z = array.get(2).getAsFloat();
        return new Vector3f(x, y, z);
    }
}
