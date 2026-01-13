package net.shinysquare.cslib.core;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.shinysquare.cslib.CutScenesLib;

/**
 * Handles pausing the game during cutscenes
 * 
 * This class listens to various game events and cancels them when a cutscene
 * is playing, effectively freezing the game state.
 * 
 * What gets paused:
 * - Entity ticking (movement, AI, physics)
 * - World ticking (block updates, weather, time)
 * - Player input (movement, interactions)
 * 
 * What doesn't get paused:
 * - Rendering (so the cutscene can be displayed)
 * - Sound (cutscenes can have audio)
 * 
 * @author ShinySquare
 */
public class GamePauseHandler {
    
    /** Whether the game is currently paused for a cutscene */
    private boolean isPaused = false;
    
    /**
     * Create a new game pause handler
     */
    public GamePauseHandler() {
        CutScenesLib.LOGGER.info("GamePauseHandler initialized");
    }
    
    /**
     * Set whether the game should be paused
     * 
     * @param paused true to pause, false to resume
     */
    public void setPaused(boolean paused) {
        this.isPaused = paused;
        
        if (paused) {
            CutScenesLib.LOGGER.debug("Game paused for cutscene");
        } else {
            CutScenesLib.LOGGER.debug("Game resumed after cutscene");
        }
    }
    
    /**
     * Check if the game is currently paused
     * 
     * @return true if paused, false otherwise
     */
    public boolean isPaused() {
        return isPaused;
    }
    
    /**
     * Cancel entity tick events when paused
     * This prevents entities from moving, updating AI, etc.
     */
    @SubscribeEvent
    public void onEntityTick(EntityTickEvent.Pre event) {
        if (isPaused) {
            event.setCanceled(true);
        }
    }
    
    /**
     * Cancel level tick events when paused
     * This prevents block updates, weather changes, time progression, etc.
     */
    @SubscribeEvent
    public void onLevelTick(LevelTickEvent.Pre event) {
        if (isPaused) {
            event.setCanceled(true);
        }
    }
    
    /**
     * Cancel living entity updates when paused
     * This is an extra layer to ensure entities don't update
     */
    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingTickEvent event) {
        if (isPaused) {
            event.setCanceled(true);
        }
    }
}
