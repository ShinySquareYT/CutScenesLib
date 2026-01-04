package net.shinysquare.cslib.render;

import net.minecraft.resources.ResourceLocation;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages dynamic texture mapping for GeckoLib bones
 */
public class BoneTextureManager {
    
    private static final Map<String, ResourceLocation> globalBoneMappings = new HashMap<>();
    
    /**
     * Map a specific bone name to a texture globally
     * 
     * @param boneName The name of the bone in Blockbench
     * @param texture The texture to apply to this bone
     */
    public static void mapTextureToBone(String boneName, ResourceLocation texture) {
        globalBoneMappings.put(boneName, texture);
    }
    
    /**
     * Get the texture for a specific bone
     * 
     * @param boneName The name of the bone
     * @param defaultTexture The default texture if no mapping exists
     * @return The texture to use
     */
    public static ResourceLocation getTextureForBone(String boneName, ResourceLocation defaultTexture) {
        return globalBoneMappings.getOrDefault(boneName, defaultTexture);
    }
    
    /**
     * Clear all global mappings
     */
    public static void clearMappings() {
        globalBoneMappings.clear();
    }
}
