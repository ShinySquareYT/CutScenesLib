package net.shinysquare.cslib.cutscene;

import net.minecraft.resources.ResourceLocation;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a cutscene with all its data
 * 
 * A cutscene contains:
 * - Metadata (name, duration, pause settings)
 * - Camera path (how the camera moves)
 * - Keyframes (animation data at specific times)
 * - Skin mapping configuration (optional)
 * 
 * This class is loaded from JSON files in assets/[modid]/cutscenes/[name]/cutscene.json
 * 
 * @author ShinySquare
 */
public class Cutscene {
    
    /** The resource location of this cutscene */
    private ResourceLocation id;
    
    /** Display name of the cutscene */
    private String name;
    
    /** Duration in seconds */
    private float duration;
    
    /** Whether to pause the game during playback */
    private boolean pauseGame;
    
    /** The camera path for this cutscene (can be loaded from external camera.json) */
    private CameraPath cameraPath;
    
    /** List of models in this scene (Blockbench models) */
    private List<SceneModel> models;
    
    /** Resource location for external camera config */
    private ResourceLocation cameraConfigLocation;
    
    /**
     * Create a new cutscene
     */
    public Cutscene() {
        this.models = new ArrayList<>();
        this.pauseGame = true;
    }

    public List<SceneModel> getModels() {
        return models;
    }

    public void addModel(SceneModel model) {
        this.models.add(model);
    }

    public ResourceLocation getCameraConfigLocation() {
        return cameraConfigLocation;
    }

    public void setCameraConfigLocation(ResourceLocation cameraConfigLocation) {
        this.cameraConfigLocation = cameraConfigLocation;
    }
    
    // Getters and setters
    
    public ResourceLocation getId() {
        return id;
    }
    
    public void setId(ResourceLocation id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public float getDuration() {
        return duration;
    }
    
    public void setDuration(float duration) {
        this.duration = duration;
    }
    
    public boolean shouldPauseGame() {
        return pauseGame;
    }
    
    public void setPauseGame(boolean pauseGame) {
        this.pauseGame = pauseGame;
    }
    
    public CameraPath getCameraPath() {
        return cameraPath;
    }
    
    public void setCameraPath(CameraPath cameraPath) {
        this.cameraPath = cameraPath;
    }
    
    public List<CutsceneFrame> getFrames() {
        return frames;
    }
    
    public void setFrames(List<CutsceneFrame> frames) {
        this.frames = frames;
    }
    
    public void addFrame(CutsceneFrame frame) {
        this.frames.add(frame);
    }
    
    public SkinMapping getSkinMapping() {
        return skinMapping;
    }
    
    public void setSkinMapping(SkinMapping skinMapping) {
        this.skinMapping = skinMapping;
    }
    
    /**
     * Get the frame at a specific time
     * 
     * This interpolates between keyframes if necessary
     * 
     * @param time The time in seconds
     * @return The frame at that time, or null if out of bounds
     */
    public CutsceneFrame getFrameAtTime(float time) {
        if (frames.isEmpty()) {
            return null;
        }
        
        // Find the two frames to interpolate between
        CutsceneFrame before = null;
        CutsceneFrame after = null;
        
        for (CutsceneFrame frame : frames) {
            if (frame.getTime() <= time) {
                before = frame;
            }
            if (frame.getTime() >= time && after == null) {
                after = frame;
            }
        }
        
        // If we're before the first frame, return the first frame
        if (before == null) {
            return frames.get(0);
        }
        
        // If we're after the last frame, return the last frame
        if (after == null) {
            return frames.get(frames.size() - 1);
        }
        
        // If they're the same frame, return it
        if (before == after) {
            return before;
        }
        
        // TODO: Implement interpolation between frames
        // For now, just return the earlier frame
        return before;
    }
    
    @Override
    public String toString() {
        return "Cutscene{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", duration=" + duration +
                ", pauseGame=" + pauseGame +
                ", frames=" + frames.size() +
                '}';
    }
}
