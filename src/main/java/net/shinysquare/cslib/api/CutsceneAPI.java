package net.shinysquare.cslib.api;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.shinysquare.cslib.CutScenesLib;
import net.shinysquare.cslib.cutscene.Cutscene;

/**
 * Main API class for CutScenesLib
 * 
 * This is the primary interface that other mods will use to interact with CutScenesLib.
 * All public methods are static for easy access.
 * 
 * Example usage:
 * <pre>
 * // Load a cutscene from your mod's assets
 * Cutscene cutscene = CutsceneAPI.loadCutscene(new ResourceLocation("mymod", "intro"));
 * 
 * // Play it for a player
 * CutsceneAPI.playCutscene(player, cutscene);
 * 
 * // Or register a trigger
 * CutsceneAPI.registerItemTrigger(Items.DIAMOND, cutscene);
 * </pre>
 * 
 * @author ShinySquare
 */
public class CutsceneAPI {
    
    /**
     * Load a cutscene from a resource location
     * 
     * The cutscene should be located at:
     * assets/[namespace]/cutscenes/[path]/cutscene.json
     * 
     * For example: new ResourceLocation("mymod", "intro")
     * looks for: assets/mymod/cutscenes/intro/cutscene.json
     * 
     * @param location The resource location of the cutscene
     * @return The loaded cutscene, or null if loading failed
     */
    public static Cutscene loadCutscene(ResourceLocation location) {
        return CutScenesLib.getInstance()
            .getCutsceneManager()
            .loadCutscene(location);
    }
    
    /**
     * Register a cutscene so it can be referenced by ID
     * 
     * This allows you to load the cutscene once and play it multiple times
     * without reloading from disk.
     * 
     * @param id The ID to register the cutscene under
     * @param cutscene The cutscene to register
     */
    public static void registerCutscene(ResourceLocation id, Cutscene cutscene) {
        CutScenesLib.getInstance()
            .getCutsceneManager()
            .registerCutscene(id, cutscene);
    }
    
    /**
     * Get a registered cutscene by ID
     * 
     * @param id The ID of the cutscene
     * @return The cutscene, or null if not found
     */
    public static Cutscene getCutscene(ResourceLocation id) {
        return CutScenesLib.getInstance()
            .getCutsceneManager()
            .getCutscene(id);
    }
    
    /**
     * Play a cutscene for a player
     * 
     * This will:
     * 1. Pause the game (if configured in the cutscene)
     * 2. Render the cutscene
     * 3. Resume the game when finished
     * 
     * @param player The player to show the cutscene to
     * @param cutscene The cutscene to play
     */
    public static void playCutscene(Player player, Cutscene cutscene) {
        CutScenesLib.getInstance()
            .getCutsceneManager()
            .playCutscene(player, cutscene);
    }
    
    /**
     * Play a registered cutscene by ID
     * 
     * @param player The player to show the cutscene to
     * @param id The ID of the cutscene to play
     * @return true if the cutscene was found and started, false otherwise
     */
    public static boolean playCutscene(Player player, ResourceLocation id) {
        Cutscene cutscene = getCutscene(id);
        if (cutscene != null) {
            playCutscene(player, cutscene);
            return true;
        }
        return false;
    }
    
    /**
     * Register a trigger that plays a cutscene when an item is picked up
     * 
     * @param item The item that triggers the cutscene
     * @param cutscene The cutscene to play
     */
    public static void registerItemTrigger(net.minecraft.world.item.Item item, Cutscene cutscene) {
        CutScenesLib.getInstance()
            .getCutsceneManager()
            .registerItemTrigger(item, cutscene);
    }
    
    /**
     * Register a trigger that plays a cutscene when an advancement is completed
     * 
     * @param advancementId The ID of the advancement
     * @param cutscene The cutscene to play
     */
    public static void registerAdvancementTrigger(ResourceLocation advancementId, Cutscene cutscene) {
        CutScenesLib.getInstance()
            .getCutsceneManager()
            .registerAdvancementTrigger(advancementId, cutscene);
    }
    
    /**
     * Check if a cutscene is currently playing for a player
     * 
     * @param player The player to check
     * @return true if a cutscene is playing, false otherwise
     */
    public static boolean isPlayingCutscene(Player player) {
        return CutScenesLib.getInstance()
            .getCutsceneManager()
            .isPlayingCutscene(player);
    }
    
    /**
     * Stop the currently playing cutscene for a player
     * 
     * @param player The player to stop the cutscene for
     */
    public static void stopCutscene(Player player) {
        CutScenesLib.getInstance()
            .getCutsceneManager()
            .stopCutscene(player);
    }
    
    /**
     * Map a specific bone name to a texture globally for all cutscenes
     * 
     * @param boneName The name of the bone in Blockbench (e.g., "test")
     * @param texture The texture to apply to this bone
     */
    public static void mapTextureToBone(String boneName, ResourceLocation texture) {
        net.shinysquare.cslib.render.BoneTextureManager.mapTextureToBone(boneName, texture);
    }
}
