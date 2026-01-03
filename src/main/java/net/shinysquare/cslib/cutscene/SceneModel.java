package net.shinysquare.cslib.cutscene;

import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;

/**
 * Represents a Blockbench model within a cutscene scene
 * 
 * Each model has:
 * - A reference to the Blockbench JSON file
 * - A reference to its texture
 * - Initial position, rotation, and scale
 * - Animation state
 * 
 * @author ShinySquare
 */
public class SceneModel {
    
    /** ID of the model in the scene */
    private String id;
    
    /** Resource location of the Blockbench JSON model */
    private ResourceLocation modelLocation;
    
    /** Resource location of the texture */
    private ResourceLocation textureLocation;
    
    /** Whether this model should use the player's skin for specific textures */
    private boolean usePlayerSkin;
    
    /** The name of the texture in Blockbench to replace with player skin */
    private String skinTextureName = "player_skin";
    
    /** Base transformation */
    private Vector3f position = new Vector3f(0, 0, 0);
    private Vector3f rotation = new Vector3f(0, 0, 0);
    private Vector3f scale = new Vector3f(1, 1, 1);
    
    /** Current animation name from Blockbench */
    private String currentAnimation;
    
    public SceneModel(String id, ResourceLocation modelLocation) {
        this.id = id;
        this.modelLocation = modelLocation;
    }
    
    // Getters and Setters
    
    public String getId() { return id; }
    
    public ResourceLocation getModelLocation() { return modelLocation; }
    
    public ResourceLocation getTextureLocation() { return textureLocation; }
    public void setTextureLocation(ResourceLocation textureLocation) { this.textureLocation = textureLocation; }
    
    public boolean isUsePlayerSkin() { return usePlayerSkin; }
    public void setUsePlayerSkin(boolean usePlayerSkin) { this.usePlayerSkin = usePlayerSkin; }
    
    public String getSkinTextureName() { return skinTextureName; }
    public void setSkinTextureName(String skinTextureName) { this.skinTextureName = skinTextureName; }
    
    public Vector3f getPosition() { return position; }
    public void setPosition(Vector3f position) { this.position = position; }
    
    public Vector3f getRotation() { return rotation; }
    public void setRotation(Vector3f rotation) { this.rotation = rotation; }
    
    public Vector3f getScale() { return scale; }
    public void setScale(Vector3f scale) { this.scale = scale; }
    
    public String getCurrentAnimation() { return currentAnimation; }
    public void setCurrentAnimation(String currentAnimation) { this.currentAnimation = currentAnimation; }
}
