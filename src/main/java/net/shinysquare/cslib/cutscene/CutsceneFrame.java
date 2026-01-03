package net.shinysquare.cslib.cutscene;

import com.mojang.math.Axis;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single keyframe in a cutscene
 * 
 * A keyframe contains:
 * - Time (when this frame occurs)
 * - List of entities/objects to render
 * - Their positions, rotations, and scales
 * 
 * Frames are interpolated during playback to create smooth animation
 * 
 * @author ShinySquare
 */
public class CutsceneFrame {
    
    /** Time in seconds when this frame occurs */
    private float time;
    
    /** List of entities in this frame */
    private List<FrameEntity> entities;
    
    /**
     * Create a new frame at the given time
     */
    public CutsceneFrame(float time) {
        this.time = time;
        this.entities = new ArrayList<>();
    }
    
    /**
     * Default constructor for JSON deserialization
     */
    public CutsceneFrame() {
        this(0.0f);
    }
    
    public float getTime() {
        return time;
    }
    
    public void setTime(float time) {
        this.time = time;
    }
    
    public List<FrameEntity> getEntities() {
        return entities;
    }
    
    public void setEntities(List<FrameEntity> entities) {
        this.entities = entities;
    }
    
    public void addEntity(FrameEntity entity) {
        this.entities.add(entity);
    }
    
    /**
     * Represents an entity/object in a frame
     */
    public static class FrameEntity {
        /** Unique ID for this entity (used for interpolation) */
        private String id;
        
        /** Model to render (e.g., "player", "cube", "custom_model") */
        private String model;
        
        /** Position in 3D space */
        private Vector3f position;
        
        /** Rotation in degrees (pitch, yaw, roll) */
        private Vector3f rotation;
        
        /** Scale (1.0 = normal size) */
        private Vector3f scale;
        
        /** Whether this entity should use player skin mapping */
        private boolean usePlayerSkin;
        
        /**
         * Create a new entity
         */
        public FrameEntity() {
            this.position = new Vector3f(0, 0, 0);
            this.rotation = new Vector3f(0, 0, 0);
            this.scale = new Vector3f(1, 1, 1);
            this.usePlayerSkin = false;
        }
        
        // Getters and setters
        
        public String getId() {
            return id;
        }
        
        public void setId(String id) {
            this.id = id;
        }
        
        public String getModel() {
            return model;
        }
        
        public void setModel(String model) {
            this.model = model;
        }
        
        public Vector3f getPosition() {
            return position;
        }
        
        public void setPosition(Vector3f position) {
            this.position = position;
        }
        
        public void setPosition(float x, float y, float z) {
            this.position.set(x, y, z);
        }
        
        public Vector3f getRotation() {
            return rotation;
        }
        
        public void setRotation(Vector3f rotation) {
            this.rotation = rotation;
        }
        
        public void setRotation(float pitch, float yaw, float roll) {
            this.rotation.set(pitch, yaw, roll);
        }
        
        public Vector3f getScale() {
            return scale;
        }
        
        public void setScale(Vector3f scale) {
            this.scale = scale;
        }
        
        public void setScale(float x, float y, float z) {
            this.scale.set(x, y, z);
        }
        
        public void setScale(float uniform) {
            this.scale.set(uniform, uniform, uniform);
        }
        
        public boolean isUsePlayerSkin() {
            return usePlayerSkin;
        }
        
        public void setUsePlayerSkin(boolean usePlayerSkin) {
            this.usePlayerSkin = usePlayerSkin;
        }
    }
}
