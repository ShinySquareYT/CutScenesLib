package net.shinysquare.cslib.core;

import net.minecraft.world.entity.player.Player;
import net.shinysquare.cslib.CutScenesLib;
import net.shinysquare.cslib.cutscene.CameraPath;
import net.shinysquare.cslib.cutscene.Cutscene;
import net.shinysquare.cslib.cutscene.CutsceneFrame;
import net.shinysquare.cslib.event.CutsceneEndEvent;
import net.shinysquare.cslib.event.CutsceneStartEvent;
import net.neoforged.neoforge.common.NeoForge;

/**
 * Plays a cutscene for a specific player
 * 
 * This class manages the playback of a single cutscene, including:
 * - Tracking playback time
 * - Updating camera position
 * - Rendering frames
 * - Pausing/resuming the game
 * - Firing events
 * 
 * @author ShinySquare
 */
public class CutscenePlayer {
    
    /** The player watching this cutscene */
    private final Player player;
    
    /** The cutscene being played */
    private final Cutscene cutscene;
    
    /** Current playback time in seconds */
    private float currentTime;
    
    /** Whether the cutscene is currently playing */
    private boolean playing;
    
    /** Whether the cutscene has finished */
    private boolean finished;
    
    /** The current camera state */
    private CameraPath.CameraState cameraState;
    
    /** The current frame */
    private CutsceneFrame currentFrame;
    
    /**
     * Create a new cutscene player
     * 
     * @param player The player to show the cutscene to
     * @param cutscene The cutscene to play
     */
    public CutscenePlayer(Player player, Cutscene cutscene) {
        this.player = player;
        this.cutscene = cutscene;
        this.currentTime = 0.0f;
        this.playing = false;
        this.finished = false;
    }
    
    /**
     * Start playing the cutscene
     */
    public void start() {
        if (playing) {
            return;
        }
        
        playing = true;
        currentTime = 0.0f;
        
        // Pause the game if configured
        if (cutscene.shouldPauseGame()) {
            CutScenesLib.getInstance().getPauseHandler().setPaused(true);
        }
        
        // Fire start event
        NeoForge.EVENT_BUS.post(new CutsceneStartEvent(player, cutscene));
        
        CutScenesLib.LOGGER.info("Started cutscene: {}", cutscene.getName());
    }
    
    /**
     * Stop playing the cutscene
     */
    public void stop() {
        if (!playing) {
            return;
        }
        
        playing = false;
        finished = true;
        
        // Resume the game
        CutScenesLib.getInstance().getPauseHandler().setPaused(false);
        
        // Fire end event
        NeoForge.EVENT_BUS.post(new CutsceneEndEvent(player, cutscene));
        
        CutScenesLib.LOGGER.info("Stopped cutscene: {}", cutscene.getName());
    }
    
    /**
     * Update the cutscene (called every tick)
     */
    public void tick() {
        if (!playing) {
            return;
        }
        
        // Update time (20 ticks per second)
        currentTime += 0.05f;
        
        // Check if finished
        if (currentTime >= cutscene.getDuration()) {
            stop();
            return;
        }
        
        // Update camera
        if (cutscene.getCameraPath() != null) {
            cameraState = cutscene.getCameraPath().getStateAtTime(currentTime);
        }
        
        // Update frame
        currentFrame = cutscene.getFrameAtTime(currentTime);
    }
    
    /**
     * Get the player watching this cutscene
     */
    public Player getPlayer() {
        return player;
    }
    
    /**
     * Get the cutscene being played
     */
    public Cutscene getCutscene() {
        return cutscene;
    }
    
    /**
     * Get the current playback time
     */
    public float getCurrentTime() {
        return currentTime;
    }
    
    /**
     * Check if the cutscene is currently playing
     */
    public boolean isPlaying() {
        return playing;
    }
    
    /**
     * Check if the cutscene has finished
     */
    public boolean isFinished() {
        return finished;
    }
    
    /**
     * Get the current camera state
     */
    public CameraPath.CameraState getCameraState() {
        return cameraState;
    }
    
    /**
     * Get the current frame
     */
    public CutsceneFrame getCurrentFrame() {
        return currentFrame;
    }
    
    /**
     * Get the playback progress (0.0 to 1.0)
     */
    public float getProgress() {
        if (cutscene.getDuration() <= 0) {
            return 1.0f;
        }
        return Math.min(currentTime / cutscene.getDuration(), 1.0f);
    }
}
