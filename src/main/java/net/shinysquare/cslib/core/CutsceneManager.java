package net.shinysquare.cslib.core;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.shinysquare.cslib.CutScenesLib;
import net.shinysquare.cslib.cutscene.Cutscene;
import net.shinysquare.cslib.loader.CutsceneLoader;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages all cutscenes in the game
 * 
 * This class handles:
 * - Loading cutscenes from files
 * - Registering cutscenes by ID
 * - Playing cutscenes for players
 * - Managing triggers (item pickups, achievements, etc.)
 * - Tracking which players are currently watching cutscenes
 * 
 * @author ShinySquare
 */
public class CutsceneManager {
    
    /** Map of registered cutscenes by ID */
    private final Map<ResourceLocation, Cutscene> cutscenes;
    
    /** Map of currently playing cutscenes by player UUID */
    private final Map<UUID, CutscenePlayer> activePlayers;
    
    /** Map of item triggers */
    private final Map<Item, Cutscene> itemTriggers;
    
    /** Map of advancement triggers */
    private final Map<ResourceLocation, Cutscene> advancementTriggers;
    
    /**
     * Create a new cutscene manager
     */
    public CutsceneManager() {
        this.cutscenes = new HashMap<>();
        this.activePlayers = new HashMap<>();
        this.itemTriggers = new HashMap<>();
        this.advancementTriggers = new HashMap<>();
        
        CutScenesLib.LOGGER.info("CutsceneManager initialized");
    }
    
    /**
     * Load a cutscene from a resource location
     * 
     * @param location The resource location of the cutscene
     * @return The loaded cutscene, or null if loading failed
     */
    public Cutscene loadCutscene(ResourceLocation location) {
        try {
            Minecraft minecraft = Minecraft.getInstance();
            ResourceManager resourceManager = minecraft.getResourceManager();
            
            Cutscene cutscene = CutsceneLoader.loadCutscene(resourceManager, location);
            
            if (cutscene != null) {
                // Automatically register the loaded cutscene
                registerCutscene(location, cutscene);
            }
            
            return cutscene;
        } catch (Exception e) {
            CutScenesLib.LOGGER.error("Failed to load cutscene: {}", location, e);
            return null;
        }
    }
    
    /**
     * Register a cutscene by ID
     * 
     * @param id The ID to register under
     * @param cutscene The cutscene to register
     */
    public void registerCutscene(ResourceLocation id, Cutscene cutscene) {
        cutscenes.put(id, cutscene);
        CutScenesLib.LOGGER.info("Registered cutscene: {}", id);
    }
    
    /**
     * Get a registered cutscene by ID
     * 
     * @param id The ID of the cutscene
     * @return The cutscene, or null if not found
     */
    public Cutscene getCutscene(ResourceLocation id) {
        return cutscenes.get(id);
    }
    
    /**
     * Play a cutscene for a player
     * 
     * @param player The player to show the cutscene to
     * @param cutscene The cutscene to play
     */
    public void playCutscene(Player player, Cutscene cutscene) {
        if (player == null || cutscene == null) {
            CutScenesLib.LOGGER.warn("Cannot play cutscene: player or cutscene is null");
            return;
        }
        
        UUID playerId = player.getUUID();
        
        // Stop any currently playing cutscene for this player
        if (activePlayers.containsKey(playerId)) {
            stopCutscene(player);
        }
        
        // Create a new cutscene player
        CutscenePlayer cutscenePlayer = new CutscenePlayer(player, cutscene);
        activePlayers.put(playerId, cutscenePlayer);
        
        // Start playing
        cutscenePlayer.start();
        
        CutScenesLib.LOGGER.info("Started cutscene {} for player {}", cutscene.getId(), player.getName().getString());
    }
    
    /**
     * Stop the currently playing cutscene for a player
     * 
     * @param player The player to stop the cutscene for
     */
    public void stopCutscene(Player player) {
        UUID playerId = player.getUUID();
        CutscenePlayer cutscenePlayer = activePlayers.get(playerId);
        
        if (cutscenePlayer != null) {
            cutscenePlayer.stop();
            activePlayers.remove(playerId);
            CutScenesLib.LOGGER.info("Stopped cutscene for player {}", player.getName().getString());
        }
    }
    
    /**
     * Check if a player is currently watching a cutscene
     * 
     * @param player The player to check
     * @return true if watching a cutscene, false otherwise
     */
    public boolean isPlayingCutscene(Player player) {
        return activePlayers.containsKey(player.getUUID());
    }
    
    /**
     * Get the cutscene player for a player
     * 
     * @param player The player
     * @return The cutscene player, or null if not watching a cutscene
     */
    public CutscenePlayer getCutscenePlayer(Player player) {
        return activePlayers.get(player.getUUID());
    }
    
    /**
     * Update all active cutscene players
     * Called every tick
     */
    public void tick() {
        // Update all active players
        activePlayers.values().forEach(CutscenePlayer::tick);
        
        // Remove finished players
        activePlayers.entrySet().removeIf(entry -> entry.getValue().isFinished());
    }
    
    /**
     * Register a trigger that plays a cutscene when an item is picked up
     * 
     * @param item The item that triggers the cutscene
     * @param cutscene The cutscene to play
     */
    public void registerItemTrigger(Item item, Cutscene cutscene) {
        itemTriggers.put(item, cutscene);
        CutScenesLib.LOGGER.info("Registered item trigger for {}", item);
    }
    
    /**
     * Register a trigger that plays a cutscene when an advancement is completed
     * 
     * @param advancementId The ID of the advancement
     * @param cutscene The cutscene to play
     */
    public void registerAdvancementTrigger(ResourceLocation advancementId, Cutscene cutscene) {
        advancementTriggers.put(advancementId, cutscene);
        CutScenesLib.LOGGER.info("Registered advancement trigger for {}", advancementId);
    }
    
    /**
     * Get the cutscene triggered by an item
     * 
     * @param item The item
     * @return The cutscene, or null if no trigger exists
     */
    public Cutscene getItemTrigger(Item item) {
        return itemTriggers.get(item);
    }
    
    /**
     * Get the cutscene triggered by an advancement
     * 
     * @param advancementId The advancement ID
     * @return The cutscene, or null if no trigger exists
     */
    public Cutscene getAdvancementTrigger(ResourceLocation advancementId) {
        return advancementTriggers.get(advancementId);
    }
}
