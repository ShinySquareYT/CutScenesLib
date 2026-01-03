package net.shinysquare.cslib.render;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.shinysquare.cslib.CutScenesLib;
import net.shinysquare.cslib.cutscene.CutsceneFrame;
import net.shinysquare.cslib.cutscene.SkinMapping;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Handles mapping player skins onto cutscene elements
 * 
 * This class:
 * - Fetches player skin textures
 * - Caches them for performance
 * - Maps specific UV regions from the skin to cutscene models
 * - Renders entities with player skins applied
 * 
 * Player skins in Minecraft are 64x64 textures with specific layouts:
 * - Head: (8, 8) to (16, 16) - front face
 * - Body: (20, 20) to (28, 32)
 * - Arms: (44, 20) to (52, 32) - right arm
 * - Legs: (4, 20) to (12, 32) - right leg
 * 
 * @author ShinySquare
 */
public class SkinTextureMapper {
    
    /** Cache of player skins by UUID */
    private final Map<UUID, ResourceLocation> skinCache;
    
    /**
     * Create a new skin texture mapper
     */
    public SkinTextureMapper() {
        this.skinCache = new HashMap<>();
        CutScenesLib.LOGGER.info("SkinTextureMapper initialized");
    }
    
    /**
     * Get the skin texture for a player
     * 
     * @param player The player
     * @return The resource location of their skin texture
     */
    public ResourceLocation getPlayerSkin(Player player) {
        UUID playerId = player.getUUID();
        
        // Check cache first
        if (skinCache.containsKey(playerId)) {
            return skinCache.get(playerId);
        }
        
        // Get the skin from player info
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.getConnection() != null) {
            PlayerInfo playerInfo = minecraft.getConnection().getPlayerInfo(playerId);
            if (playerInfo != null) {
                ResourceLocation skin = playerInfo.getSkin().texture();
                skinCache.put(playerId, skin);
                return skin;
            }
        }
        
        // Fall back to default skin
        ResourceLocation defaultSkin = DefaultPlayerSkin.get(playerId);
        skinCache.put(playerId, defaultSkin);
        return defaultSkin;
    }
    
    /**
     * Get the texture to use for a specific model part
     * 
     * @param model The scene model being rendered
     * @param textureName The name of the texture in the model
     * @param player The player watching the cutscene
     * @return The resource location of the texture to use
     */
    public ResourceLocation getTextureForPart(SceneModel model, String textureName, Player player) {
        // If this model uses player skin and this texture matches the skin texture name
        if (model.isUsePlayerSkin() && textureName.equals(model.getSkinTextureName())) {
            return getPlayerSkin(player);
        }
        
        // Otherwise use the model's default texture
        return model.getTextureLocation();
    }
    
    /**
     * Render a player model with skin texture
     */
    private void renderPlayerModel(GuiGraphics graphics, ResourceLocation skinTexture) {
        // Render head
        // In Minecraft, the head texture is at (8, 8) to (16, 16) on a 64x64 skin
        // We'll render it as a simple textured quad
        
        // For a full implementation, you would:
        // 1. Bind the skin texture
        // 2. Render a 3D cube with proper UV mapping
        // 3. Apply transformations for animation
        
        // Placeholder: render colored rectangles
        graphics.fill(-4, -16, 4, -8, 0xFFFFDDDD); // Head
        graphics.fill(-4, -8, 4, 8, 0xFFAAAAFF);   // Body
        graphics.fill(-6, -8, -4, 8, 0xFF8888FF);  // Left arm
        graphics.fill(4, -8, 6, 8, 0xFF8888FF);    // Right arm
        graphics.fill(-2, 8, 0, 16, 0xFF6666FF);   // Left leg
        graphics.fill(0, 8, 2, 16, 0xFF6666FF);    // Right leg
    }
    
    /**
     * Render a generic model with skin texture
     */
    private void renderGenericModel(GuiGraphics graphics, ResourceLocation skinTexture) {
        // Placeholder implementation
        graphics.fill(-5, -5, 5, 5, 0xFFCCCCCC);
    }
    
    /**
     * Apply skin mapping to a specific zone
     * 
     * This maps a region of the player's skin texture to a region on the model
     * 
     * @param zone The skin zone configuration
     * @param skinTexture The player's skin texture
     * @return A modified texture with the skin applied
     */
    public ResourceLocation applySkinMapping(SkinMapping.SkinZone zone, ResourceLocation skinTexture) {
        // TODO: Implement actual texture manipulation
        // This would involve:
        // 1. Loading the skin texture as a NativeImage
        // 2. Extracting the specified UV region
        // 3. Creating a new texture with the skin region applied
        // 4. Registering it with the texture manager
        
        CutScenesLib.LOGGER.debug("Applying skin mapping for zone: {}", zone.getName());
        return skinTexture;
    }
    
    /**
     * Clear the skin cache
     * Useful when players leave or skins are updated
     */
    public void clearCache() {
        skinCache.clear();
        CutScenesLib.LOGGER.debug("Skin cache cleared");
    }
    
    /**
     * Remove a specific player from the cache
     * 
     * @param playerId The player's UUID
     */
    public void removeCached(UUID playerId) {
        skinCache.remove(playerId);
    }
}
