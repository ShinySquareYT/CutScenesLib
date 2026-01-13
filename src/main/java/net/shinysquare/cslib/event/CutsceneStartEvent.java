package net.shinysquare.cslib.event;

import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import net.shinysquare.cslib.cutscene.Cutscene;

/**
 * Event fired when a cutscene starts playing
 * 
 * This event is cancellable - if cancelled, the cutscene will not play.
 * Other mods can listen to this event to:
 * - Prevent certain cutscenes from playing
 * - Perform actions when a cutscene starts
 * - Modify the cutscene before it plays
 * 
 * @author ShinySquare
 */
public class CutsceneStartEvent extends Event implements ICancellableEvent {
    
    private final Player player;
    private final Cutscene cutscene;
    
    /**
     * Create a new cutscene start event
     * 
     * @param player The player watching the cutscene
     * @param cutscene The cutscene that is starting
     */
    public CutsceneStartEvent(Player player, Cutscene cutscene) {
        this.player = player;
        this.cutscene = cutscene;
    }
    
    /**
     * Get the player watching the cutscene
     */
    public Player getPlayer() {
        return player;
    }
    
    /**
     * Get the cutscene that is starting
     */
    public Cutscene getCutscene() {
        return cutscene;
    }
}
