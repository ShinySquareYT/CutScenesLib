package net.shinysquare.cslib.render;

import net.minecraft.resources.ResourceLocation;
import net.shinysquare.cslib.cutscene.GeckoSceneModel;
import software.bernie.geckolib.model.DefaultedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * A custom GeckoLib renderer that supports our BoneTextureRenderLayer
 */
public class GeckoSceneRenderer extends software.bernie.geckolib.renderer.GeoObjectRenderer<GeckoSceneModel> {
    
    public GeckoSceneRenderer(ResourceLocation model) {
        super(new DefaultedGeoModel<>(model));
        
        // Add our custom render layer for bone-based texture swapping
        addRenderLayer(new BoneTextureRenderLayer(this));
    }
}
