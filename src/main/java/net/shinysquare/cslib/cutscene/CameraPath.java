package net.shinysquare.cslib.cutscene;

import org.joml.Vector3f;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the camera movement path during a cutscene
 * 
 * The camera path is defined by keyframes that specify position and rotation
 * at specific times. The camera smoothly interpolates between these keyframes.
 * 
 * @author ShinySquare
 */
public class CameraPath {
    
    /** Type of camera movement */
    private CameraType type;
    
    /** List of camera keyframes */
    private List<CameraKeyframe> keyframes;
    
    /**
     * Create a new camera path
     */
    public CameraPath() {
        this.type = CameraType.PATH;
        this.keyframes = new ArrayList<>();
    }
    
    public CameraType getType() {
        return type;
    }
    
    public void setType(CameraType type) {
        this.type = type;
    }
    
    public List<CameraKeyframe> getKeyframes() {
        return keyframes;
    }
    
    public void setKeyframes(List<CameraKeyframe> keyframes) {
        this.keyframes = keyframes;
    }
    
    public void addKeyframe(CameraKeyframe keyframe) {
        this.keyframes.add(keyframe);
    }
    
    /**
     * Get the camera position and rotation at a specific time
     * 
     * @param time The time in seconds
     * @return The camera state at that time
     */
    public CameraState getStateAtTime(float time) {
        if (keyframes.isEmpty()) {
            return new CameraState(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0));
        }
        
        // Find the two keyframes to interpolate between
        CameraKeyframe before = null;
        CameraKeyframe after = null;
        
        for (CameraKeyframe keyframe : keyframes) {
            if (keyframe.getTime() <= time) {
                before = keyframe;
            }
            if (keyframe.getTime() >= time && after == null) {
                after = keyframe;
            }
        }
        
        // If we're before the first keyframe, return the first keyframe
        if (before == null) {
            CameraKeyframe first = keyframes.get(0);
            return new CameraState(first.getPosition(), first.getRotation());
        }
        
        // If we're after the last keyframe, return the last keyframe
        if (after == null) {
            CameraKeyframe last = keyframes.get(keyframes.size() - 1);
            return new CameraState(last.getPosition(), last.getRotation());
        }
        
        // If they're the same keyframe, return it
        if (before == after) {
            return new CameraState(before.getPosition(), before.getRotation());
        }
        
        // Interpolate between the two keyframes
        float t = (time - before.getTime()) / (after.getTime() - before.getTime());
        
        Vector3f position = new Vector3f();
        before.getPosition().lerp(after.getPosition(), t, position);
        
        Vector3f rotation = new Vector3f();
        before.getRotation().lerp(after.getRotation(), t, rotation);
        
        return new CameraState(position, rotation);
    }
    
    /**
     * Camera movement type
     */
    public enum CameraType {
        /** Camera follows a predefined path */
        PATH,
        /** Camera is fixed at one position */
        FIXED,
        /** Camera follows an entity */
        FOLLOW
    }
    
    /**
     * A single camera keyframe
     */
    public static class CameraKeyframe {
        private float time;
        private Vector3f position;
        private Vector3f rotation; // pitch, yaw, roll
        
        public CameraKeyframe() {
            this.position = new Vector3f(0, 0, 0);
            this.rotation = new Vector3f(0, 0, 0);
        }
        
        public CameraKeyframe(float time, Vector3f position, Vector3f rotation) {
            this.time = time;
            this.position = position;
            this.rotation = rotation;
        }
        
        public float getTime() {
            return time;
        }
        
        public void setTime(float time) {
            this.time = time;
        }
        
        public Vector3f getPosition() {
            return position;
        }
        
        public void setPosition(Vector3f position) {
            this.position = position;
        }
        
        public Vector3f getRotation() {
            return rotation;
        }
        
        public void setRotation(Vector3f rotation) {
            this.rotation = rotation;
        }
    }
    
    /**
     * Represents the camera state at a specific moment
     */
    public static class CameraState {
        private final Vector3f position;
        private final Vector3f rotation;
        
        public CameraState(Vector3f position, Vector3f rotation) {
            this.position = position;
            this.rotation = rotation;
        }
        
        public Vector3f getPosition() {
            return position;
        }
        
        public Vector3f getRotation() {
            return rotation;
        }
    }
}
