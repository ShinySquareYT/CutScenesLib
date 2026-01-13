package net.shinysquare.cslib.event;

import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;
import net.shinysquare.cslib.cutscene.Cutscene;

/**
 * Event fired when a cutscene finishes playing
 * 
 * Other mods can listen to this event to:
 * - Perform actions when a cutscene ends
 * - Give rewards after watching a cutscene
 * - Trigger other game events
 * 
 * @author ShinySquare
 */
public class CutsceneEndEvent extends Event {
    
    private final Player player;
    private final Cutscene cutscene;
    
    /**
     * Create a new cutscene end event
     * 
     * @param player The player who watched the cutscene
     * @param cutscene The cutscene that finished
     */
    public CutsceneEndEvent(Player player, Cutscene cutscene) {
        this.player = player;
        this.cutscene = cutscene;
    }
    
    /**
     * Get the player who watched the cutscene
     */
    public Player getPlayer() {
        return player;
    }
    
    /**
     * Get the cutscene that finished
     */
    public Cutscene getCutscene() {
        return cutscene;
    }
}
