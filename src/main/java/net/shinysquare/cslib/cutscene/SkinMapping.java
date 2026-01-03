package net.shinysquare.cslib.cutscene;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration for mapping player skins onto cutscene elements
 * 
 * This allows cutscenes to be personalized by showing the player's actual skin
 * on specific parts of the cutscene (e.g., on a character's face or body)
 * 
 * @author ShinySquare
 */
public class SkinMapping {
    
    /** Whether skin mapping is enabled for this cutscene */
    private boolean enabled;
    
    /** List of zones that should use the player's skin */
    private List<SkinZone> zones;
    
    /**
     * Create a new skin mapping configuration
     */
    public SkinMapping() {
        this.enabled = false;
        this.zones = new ArrayList<>();
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public List<SkinZone> getZones() {
        return zones;
    }
    
    public void setZones(List<SkinZone> zones) {
        this.zones = zones;
    }
    
    public void addZone(SkinZone zone) {
        this.zones.add(zone);
    }
    
    /**
     * Represents a zone that should use the player's skin
     */
    public static class SkinZone {
        /** Name/ID of this zone (e.g., "player_face", "player_body") */
        private String name;
        
        /** Entity ID that this zone applies to */
        private String entityId;
        
        /** UV coordinates on the player's skin texture to sample from */
        private float skinUStart;
        private float skinVStart;
        private float skinUEnd;
        private float skinVEnd;
        
        /** UV coordinates on the cutscene model to apply to */
        private float modelUStart;
        private float modelVStart;
        private float modelUEnd;
        private float modelVEnd;
        
        /**
         * Create a new skin zone
         */
        public SkinZone() {
        }
        
        /**
         * Create a new skin zone with full configuration
         */
        public SkinZone(String name, String entityId,
                       float skinUStart, float skinVStart, float skinUEnd, float skinVEnd,
                       float modelUStart, float modelVStart, float modelUEnd, float modelVEnd) {
            this.name = name;
            this.entityId = entityId;
            this.skinUStart = skinUStart;
            this.skinVStart = skinVStart;
            this.skinUEnd = skinUEnd;
            this.skinVEnd = skinVEnd;
            this.modelUStart = modelUStart;
            this.modelVStart = modelVStart;
            this.modelUEnd = modelUEnd;
            this.modelVEnd = modelVEnd;
        }
        
        // Getters and setters
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getEntityId() {
            return entityId;
        }
        
        public void setEntityId(String entityId) {
            this.entityId = entityId;
        }
        
        public float getSkinUStart() {
            return skinUStart;
        }
        
        public void setSkinUStart(float skinUStart) {
            this.skinUStart = skinUStart;
        }
        
        public float getSkinVStart() {
            return skinVStart;
        }
        
        public void setSkinVStart(float skinVStart) {
            this.skinVStart = skinVStart;
        }
        
        public float getSkinUEnd() {
            return skinUEnd;
        }
        
        public void setSkinUEnd(float skinUEnd) {
            this.skinUEnd = skinUEnd;
        }
        
        public float getSkinVEnd() {
            return skinVEnd;
        }
        
        public void setSkinVEnd(float skinVEnd) {
            this.skinVEnd = skinVEnd;
        }
        
        public float getModelUStart() {
            return modelUStart;
        }
        
        public void setModelUStart(float modelUStart) {
            this.modelUStart = modelUStart;
        }
        
        public float getModelVStart() {
            return modelVStart;
        }
        
        public void setModelVStart(float modelVStart) {
            this.modelVStart = modelVStart;
        }
        
        public float getModelUEnd() {
            return modelUEnd;
        }
        
        public void setModelUEnd(float modelUEnd) {
            this.modelUEnd = modelUEnd;
        }
        
        public float getModelVEnd() {
            return modelVEnd;
        }
        
        public void setModelVEnd(float modelVEnd) {
            this.modelVEnd = modelVEnd;
        }
    }
}
