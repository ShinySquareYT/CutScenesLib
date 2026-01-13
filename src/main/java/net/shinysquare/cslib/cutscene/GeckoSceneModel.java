package net.shinysquare.cslib.cutscene;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * A GeckoLib-powered model for cutscenes
 */
public class GeckoSceneModel implements GeoAnimatable {
    
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final String id;
    private final ResourceLocation geoPath;
    private final ResourceLocation animPath;
    private ResourceLocation texturePath;
    
    /** Map of bone names to custom textures (Dynamic UV Mapping) */
    private final Map<String, ResourceLocation> boneTextures = new HashMap<>();
    
    private String currentAnimation;
    
    public GeckoSceneModel(String id, ResourceLocation geoPath, ResourceLocation animPath, ResourceLocation texturePath) {
        this.id = id;
        this.geoPath = geoPath;
        this.animPath = animPath;
        this.texturePath = texturePath;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> {
            if (currentAnimation != null) {
                return state.setAndContinue(RawAnimation.begin().thenPlay(currentAnimation));
            }
            return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public double getTick(Object o) {
        return 0; // Handled by cutscene player
    }
    
    // Dynamic Texture Mapping API
    
    public void mapTextureToBone(String boneName, ResourceLocation texture) {
        this.boneTextures.put(boneName, texture);
    }
    
    public ResourceLocation getTextureForBone(String boneName) {
        return boneTextures.getOrDefault(boneName, texturePath);
    }
    
    // Getters and Setters
    
    public String getId() { return id; }
    public ResourceLocation getGeoPath() { return geoPath; }
    public ResourceLocation getAnimPath() { return animPath; }
    public ResourceLocation getTexturePath() { return texturePath; }
    public void setTexturePath(ResourceLocation texturePath) { this.texturePath = texturePath; }
    public void setCurrentAnimation(String animation) { this.currentAnimation = animation; }
}
