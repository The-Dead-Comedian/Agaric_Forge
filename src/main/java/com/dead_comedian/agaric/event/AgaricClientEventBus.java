package com.dead_comedian.agaric.event;

import com.dead_comedian.agaric.AgaricMod;
import com.dead_comedian.agaric.entity.client.AgaricModelLayers;
import com.dead_comedian.agaric.entity.client.models.SporderModel;
import com.dead_comedian.agaric.particle.AgaricParticles;
import com.dead_comedian.agaric.particle.SleepParticles;
import net.minecraft.world.entity.ai.behavior.SleepInBed;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AgaricMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class AgaricClientEventBus {
    @SubscribeEvent
    public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(AgaricModelLayers.SPORDER_LAYER, SporderModel::createBodyLayer);
    }
    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(AgaricParticles.SLEEP.get(), SleepParticles.Provider::new);
    }
}
