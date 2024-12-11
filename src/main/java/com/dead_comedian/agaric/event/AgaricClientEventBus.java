package com.dead_comedian.agaric.event;

import com.dead_comedian.agaric.AgaricMod;
import com.dead_comedian.agaric.entity.client.AgaricModelLayers;
import com.dead_comedian.agaric.entity.client.models.SporderModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AgaricMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class AgaricClientEventBus {
    @SubscribeEvent
    public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(AgaricModelLayers.SPORDER_LAYER, SporderModel::createBodyLayer);
    }
}
