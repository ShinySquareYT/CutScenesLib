package net.shinysquare.cslib.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.shinysquare.cslib.CutScenesLib;
import net.shinysquare.cslib.core.CutscenePlayer;
import net.shinysquare.cslib.cutscene.CameraPath;
import net.shinysquare.cslib.cutscene.CutsceneFrame;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Renders cutscenes on screen
 * 
 * This class handles all the visual rendering of cutscenes, including:
 * - Setting up the camera
 * - Rendering 3D models and entities
 * - Applying player skin textures
 * - Drawing the cutscene overlay
 * 
 * The cutscene is rendered as an overlay on top of the game world,
 * similar to how the pause menu or inventory screen is rendered.
 * 
 * @author ShinySquare
 */
public class CutsceneRenderer {
    
    /** Singleton instance */
    private static CutsceneRenderer instance;
    
    /** The skin texture mapper */
    private final SkinTextureMapper skinMapper;
    
    /**
     * Create a new cutscene renderer
     */
    public CutsceneRenderer() {
        this.skinMapper = new SkinTextureMapper();
        instance = this;
        CutScenesLib.LOGGER.info("CutsceneRenderer initialized");
    }
    
    /**
     * Get the singleton instance
     */
    public static CutsceneRenderer getInstance() {
        if (instance == null) {
            instance = new CutsceneRenderer();
        }
        return instance;
    }
    
    /**
     * Render the cutscene overlay
     * This is called every frame when rendering the GUI
     */
    @SubscribeEvent
    public void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        
        if (player == null) {
            return;
        }
        
        // Check if this player is watching a cutscene
        CutscenePlayer cutscenePlayer = CutScenesLib.getInstance()
                .getCutsceneManager()
                .getCutscenePlayer(player);
        
        if (cutscenePlayer == null || !cutscenePlayer.isPlaying()) {
            return;
        }
        
        // Render the cutscene
        renderCutscene(event.getGuiGraphics(), cutscenePlayer, event.getPartialTick());
    }
    
    /**
     * Render a cutscene
     * 
     * @param graphics The GUI graphics context
     * @param cutscenePlayer The cutscene player
     * @param partialTick Partial tick for smooth interpolation
     */
    private void renderCutscene(GuiGraphics graphics, CutscenePlayer cutscenePlayer, float partialTick) {
        PoseStack poseStack = graphics.pose();
        
        // Get current frame and camera state
        CutsceneFrame currentFrame = cutscenePlayer.getCurrentFrame();
        CameraPath.CameraState cameraState = cutscenePlayer.getCameraState();
        
        if (currentFrame == null) {
            return;
        }
        
        // Fill screen with black background
        int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
        graphics.fill(0, 0, screenWidth, screenHeight, 0xFF000000);
        
        poseStack.pushPose();
        
        // Set up 3D rendering space
        setupCamera(poseStack, cameraState, screenWidth, screenHeight);
        
        // Render all entities in the frame
        for (CutsceneFrame.FrameEntity entity : currentFrame.getEntities()) {
            renderEntity(graphics, entity, cutscenePlayer.getPlayer());
        }
        
        poseStack.popPose();
        
        // Render progress bar at bottom
        renderProgressBar(graphics, cutscenePlayer, screenWidth, screenHeight);
    }
    
    /**
     * Set up the camera for 3D rendering
     */
    private void setupCamera(PoseStack poseStack, CameraPath.CameraState cameraState, int screenWidth, int screenHeight) {
        if (cameraState == null) {
            return;
        }
        
        // Center the rendering
        poseStack.translate(screenWidth / 2.0, screenHeight / 2.0, 0);
        
        // Apply camera rotation
        Vector3f rotation = cameraState.getRotation();
        poseStack.mulPose(org.joml.Quaternionf.fromAxisAngleDeg(1, 0, 0, rotation.x));
        poseStack.mulPose(org.joml.Quaternionf.fromAxisAngleDeg(0, 1, 0, rotation.y));
        poseStack.mulPose(org.joml.Quaternionf.fromAxisAngleDeg(0, 0, 1, rotation.z));
        
        // Apply camera position (translate in opposite direction)
        Vector3f position = cameraState.getPosition();
        poseStack.translate(-position.x * 20, -position.y * 20, -position.z * 20);
    }
    
    /**
     * Render a single entity in the cutscene
     */
    private void renderEntity(GuiGraphics graphics, CutsceneFrame.FrameEntity entity, Player player) {
        PoseStack poseStack = graphics.pose();
        
        poseStack.pushPose();
        
        // Apply entity transformations
        Vector3f pos = entity.getPosition();
        poseStack.translate(pos.x * 20, pos.y * 20, pos.z * 20);
        
        Vector3f rot = entity.getRotation();
        poseStack.mulPose(org.joml.Quaternionf.fromAxisAngleDeg(1, 0, 0, rot.x));
        poseStack.mulPose(org.joml.Quaternionf.fromAxisAngleDeg(0, 1, 0, rot.y));
        poseStack.mulPose(org.joml.Quaternionf.fromAxisAngleDeg(0, 0, 1, rot.z));
        
        Vector3f scale = entity.getScale();
        poseStack.scale(scale.x, scale.y, scale.z);
        
        // Render the model
        if (entity.isUsePlayerSkin()) {
            // Render with player skin
            skinMapper.renderWithPlayerSkin(graphics, entity, player);
        } else {
            // Render normal model
            renderModel(graphics, entity);
        }
        
        poseStack.popPose();
    }
    
    /**
     * Render a model (placeholder implementation)
     * 
     * In a full implementation, this would load and render actual 3D models.
     * For now, we'll render simple colored cubes as placeholders.
     */
    private void renderModel(GuiGraphics graphics, CutsceneFrame.FrameEntity entity) {
        // TODO: Implement actual 3D model rendering
        // For now, render a simple placeholder
        
        String model = entity.getModel();
        if ("player".equals(model)) {
            // Render a player-shaped placeholder
            graphics.fill(-4, -12, 4, 12, 0xFFFFAAAA); // Body
            graphics.fill(-3, -16, 3, -12, 0xFFFFDDDD); // Head
        } else {
            // Render a generic cube
            graphics.fill(-5, -5, 5, 5, 0xFFAAAAAA);
        }
    }
    
    /**
     * Render the progress bar at the bottom of the screen
     */
    private void renderProgressBar(GuiGraphics graphics, CutscenePlayer cutscenePlayer, int screenWidth, int screenHeight) {
        int barHeight = 4;
        int barY = screenHeight - barHeight - 10;
        int barWidth = screenWidth - 40;
        int barX = 20;
        
        // Background
        graphics.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF333333);
        
        // Progress
        float progress = cutscenePlayer.getProgress();
        int progressWidth = (int) (barWidth * progress);
        graphics.fill(barX, barY, barX + progressWidth, barY + barHeight, 0xFFFFFFFF);
        
        // Time text
        String timeText = String.format("%.1f / %.1f", 
                cutscenePlayer.getCurrentTime(), 
                cutscenePlayer.getCutscene().getDuration());
        graphics.drawString(Minecraft.getInstance().font, timeText, 
                barX, barY - 10, 0xFFFFFFFF);
    }
}
