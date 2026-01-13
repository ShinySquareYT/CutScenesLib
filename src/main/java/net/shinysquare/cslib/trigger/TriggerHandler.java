package net.shinysquare.cslib.trigger;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.neoforged.neoforge.event.entity.player.EntityItemPickupEvent;
import net.shinysquare.cslib.CutScenesLib;
import net.shinysquare.cslib.cutscene.Cutscene;

/**
 * Handles automatic triggering of cutscenes based on game events
 * 
 * This class listens to various Minecraft events and automatically plays
 * cutscenes when the configured triggers occur.
 * 
 * Supported triggers:
 * - Item pickup: Play cutscene when player picks up a specific item
 * - Advancement: Play cutscene when player completes an advancement
 * - Custom: Other mods can fire custom trigger events
 * 
 * @author ShinySquare
 */
public class TriggerHandler {
    
    /**
     * Create a new trigger handler
     */
    public TriggerHandler() {
        CutScenesLib.LOGGER.info("TriggerHandler initialized");
    }
    
    /**
     * Handle item pickup events
     * 
     * When a player picks up an item, check if there's a cutscene trigger
     * registered for that item. If so, play the cutscene.
     */
    @SubscribeEvent
    public void onItemPickup(EntityItemPickupEvent event) {
        Player player = event.getEntity();
        ItemEntity itemEntity = event.getItem();
        ItemStack itemStack = itemEntity.getItem();
        Item item = itemStack.getItem();
        
        // Check if there's a trigger for this item
        Cutscene cutscene = CutScenesLib.getInstance()
                .getCutsceneManager()
                .getItemTrigger(item);
        
        if (cutscene != null) {
            CutScenesLib.LOGGER.info("Item trigger activated: {} for player {}", 
                    item, player.getName().getString());
            
            // Play the cutscene
            CutScenesLib.getInstance()
                    .getCutsceneManager()
                    .playCutscene(player, cutscene);
        }
    }
    
    /**
     * Handle advancement completion events
     * 
     * When a player completes an advancement, check if there's a cutscene
     * trigger registered for that advancement. If so, play the cutscene.
     */
    @SubscribeEvent
    public void onAdvancementEarned(AdvancementEvent.AdvancementEarnEvent event) {
        Player player = event.getEntity();
        AdvancementHolder advancement = event.getAdvancement();
        ResourceLocation advancementId = advancement.id();
        
        // Check if there's a trigger for this advancement
        Cutscene cutscene = CutScenesLib.getInstance()
                .getCutsceneManager()
                .getAdvancementTrigger(advancementId);
        
        if (cutscene != null) {
            CutScenesLib.LOGGER.info("Advancement trigger activated: {} for player {}", 
                    advancementId, player.getName().getString());
            
            // Play the cutscene
            CutScenesLib.getInstance()
                    .getCutsceneManager()
                    .playCutscene(player, cutscene);
        }
    }
}
