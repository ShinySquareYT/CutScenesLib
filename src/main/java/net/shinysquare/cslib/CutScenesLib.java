package net.shinysquare.cslib;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.shinysquare.cslib.core.CutsceneManager;
import net.shinysquare.cslib.core.GamePauseHandler;
import net.shinysquare.cslib.render.CutsceneRenderer;
import net.shinysquare.cslib.trigger.TriggerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CutScenesLib - A library mod for creating pausable 3D cutscenes in Minecraft
 * 
 * This is the main mod class that initializes all systems and provides
 * the entry point for the library.
 * 
 * @author ShinySquare
 */
@Mod(CutScenesLib.MOD_ID)
public class CutScenesLib {
    
    /** The mod ID - used throughout the mod for registration */
    public static final String MOD_ID = "cslib";
    
    /** Logger for debugging and information */
    public static final Logger LOGGER = LoggerFactory.getLogger("CutScenesLib");
    
    /** Singleton instance of the mod */
    private static CutScenesLib instance;
    
    /** The cutscene manager handles registration and lifecycle */
    private final CutsceneManager cutsceneManager;
    
    /** The game pause handler manages game state during cutscenes */
    private final GamePauseHandler pauseHandler;
    
    /** The trigger handler manages automatic cutscene triggers */
    private final TriggerHandler triggerHandler;
    
    /** The recording manager handles player and world recording */
    private final net.shinysquare.cslib.core.RecordingManager recordingManager;
    
    /** The renderer handles cutscene rendering */
    private CutsceneRenderer renderer;
    
    /**
     * Constructor - called by NeoForge when the mod is loaded
     * 
     * @param modEventBus The mod-specific event bus for lifecycle events
     */
    public CutScenesLib(IEventBus modEventBus) {
        instance = this;
        
        LOGGER.info("Initializing CutScenesLib...");
        
        // Initialize managers
        this.cutsceneManager = new CutsceneManager();
        this.pauseHandler = new GamePauseHandler();
        this.triggerHandler = new TriggerHandler();
        this.recordingManager = new net.shinysquare.cslib.core.RecordingManager();
        
        // Register lifecycle events
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
        
        // Register to the game event bus for gameplay events
        NeoForge.EVENT_BUS.register(pauseHandler);
        NeoForge.EVENT_BUS.register(triggerHandler);
        NeoForge.EVENT_BUS.register(recordingManager);
        NeoForge.EVENT_BUS.addListener(this::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(this::onRegisterCommands);
        
        LOGGER.info("CutScenesLib initialized successfully!");
    }
    
    /**
     * Common setup - runs on both client and server
     * This is where you initialize things that work on both sides
     */
    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("CutScenesLib common setup");
    }
    
    /**
     * Client setup - runs only on the client side
     * This is where you initialize rendering and client-only systems
     */
    private void clientSetup(final FMLClientSetupEvent event) {
        LOGGER.info("CutScenesLib client setup");
        
        // Initialize renderer on client
        this.renderer = new CutsceneRenderer();
        NeoForge.EVENT_BUS.register(renderer);
    }
    
    /**
     * Tick the cutscene manager every player tick
     * This updates all active cutscenes
     */
    private void onPlayerTick(PlayerTickEvent.Post event) {
        // Only tick on client side
        if (event.getEntity().level().isClientSide) {
            cutsceneManager.tick();
        }
    }
    
    /**
     * Get the singleton instance of the mod
     * 
     * @return The mod instance
     */
    public static CutScenesLib getInstance() {
        return instance;
    }
    
    /**
     * Get the cutscene manager
     * 
     * @return The cutscene manager
     */
    public CutsceneManager getCutsceneManager() {
        return cutsceneManager;
    }
    
    /**
     * Get the game pause handler
     * 
     * @return The pause handler
     */
    public GamePauseHandler getPauseHandler() {
        return pauseHandler;
    }
    
    /**
     * Get the trigger handler
     * 
     * @return The trigger handler
     */
    public TriggerHandler getTriggerHandler() {
        return triggerHandler;
    }
    
    /**
     * Get the recording manager
     * 
     * @return The recording manager
     */
    public net.shinysquare.cslib.core.RecordingManager getRecordingManager() {
        return recordingManager;
    }
    
    /**
     * Get the renderer (client-side only)
     * 
     * @return The renderer, or null if on server
     */
    public CutsceneRenderer getRenderer() {
        return renderer;
    }

    private void onRegisterCommands(net.neoforged.neoforge.event.RegisterCommandsEvent event) {
        net.shinysquare.cslib.core.RecordingCommand.register(event.getDispatcher());
    }
}
