package net.shinysquare.cslib.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.shinysquare.cslib.cutscene.GeckoSceneModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

/**
 * A native GeckoLib RenderLayer that swaps textures for specific bones
 */
public class BoneTextureRenderLayer extends GeoRenderLayer<GeckoSceneModel> {
    
    public BoneTextureRenderLayer(GeoRenderer<GeckoSceneModel> entityRenderer) {
        super(entityRenderer);
    }

    @Override
    public void renderForBone(PoseStack poseStack, GeckoSceneModel animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        // Check if this bone has a custom texture mapping
        ResourceLocation customTexture = animatable.getTextureForBone(bone.getName());
        
        // If it's different from the main model texture, we re-render this bone with the custom texture
        if (customTexture != null && !customTexture.equals(animatable.getTexturePath())) {
            RenderType customRenderType = RenderType.entityCutoutNoCull(customTexture);
            VertexConsumer customBuffer = bufferSource.getBuffer(customRenderType);
            
            // Call the renderer's internal bone rendering logic with our new texture
            this.getRenderer().renderChildBones(poseStack, animatable, bone, customRenderType, bufferSource, customBuffer, false, partialTick, packedLight, packedOverlay, 1, 1, 1, 1);
        }
    }
}
