package com.dead_comedian.agaric.entity.client.renderer;

import com.dead_comedian.agaric.AgaricMod;
import com.dead_comedian.agaric.entity.SporderEntity;
import com.dead_comedian.agaric.entity.client.AgaricModelLayers;
import com.dead_comedian.agaric.entity.client.models.SporderModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.SaddleLayer;
import net.minecraft.resources.ResourceLocation;

public class SporderRenderer extends MobRenderer<SporderEntity, SporderModel<SporderEntity>> {
    public SporderRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new SporderModel<>(pContext.bakeLayer(AgaricModelLayers.SPORDER_LAYER)), 0.75f);
        this.addLayer(new SaddleLayer(this, new SporderModel(pContext.bakeLayer(AgaricModelLayers.SPORDER_SADDLE_LAYER)), new ResourceLocation(AgaricMod.MOD_ID, "textures/entity/sporder_saddle.png")));

    }

    @Override
    public ResourceLocation getTextureLocation(SporderEntity pEntity) {
        return new ResourceLocation(AgaricMod.MOD_ID, "textures/entity/sporder.png");
    }


    @Override
    public void render(SporderEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack,
                       MultiBufferSource pBuffer, int pPackedLight) {
        if(pEntity.isBaby()) {
            pMatrixStack.scale(0.5f, 0.5f, 0.5f);
        }

        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
    }
}
