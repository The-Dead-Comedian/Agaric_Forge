package com.dead_comedian.agaric.entity.client.renderer;

import com.dead_comedian.agaric.AgaricMod;
import com.dead_comedian.agaric.entity.AgarachnidEntity;
import com.dead_comedian.agaric.entity.SporderEntity;
import com.dead_comedian.agaric.entity.client.AgaricModelLayers;
import com.dead_comedian.agaric.entity.client.models.AgarachnidModel;
import com.dead_comedian.agaric.entity.client.models.SporderModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.SaddleLayer;
import net.minecraft.resources.ResourceLocation;

public class AgarachnidRenderer extends MobRenderer<AgarachnidEntity, AgarachnidModel<AgarachnidEntity>> {
    public AgarachnidRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new AgarachnidModel<>(pContext.bakeLayer(AgaricModelLayers.AGARACHNID_LAYER)), 0.75f);

    }

    @Override
    public ResourceLocation getTextureLocation(AgarachnidEntity pEntity) {
        return new ResourceLocation(AgaricMod.MOD_ID, "textures/entity/agarachnid.png");
    }


    @Override
    public void render(AgarachnidEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack,
                       MultiBufferSource pBuffer, int pPackedLight) {
        if(pEntity.isBaby()) {
            pMatrixStack.scale(0.5f, 0.5f, 0.5f);
        }

        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
    }
}
