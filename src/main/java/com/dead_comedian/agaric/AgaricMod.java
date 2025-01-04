package com.dead_comedian.agaric;

import com.dead_comedian.agaric.block.AgaricBlocks;
import com.dead_comedian.agaric.entity.AgaricEntities;
import com.dead_comedian.agaric.entity.client.renderer.AgarachnidRenderer;
import com.dead_comedian.agaric.entity.client.renderer.SporderRenderer;
import com.dead_comedian.agaric.item.AgaricCreativeTab;
import com.dead_comedian.agaric.item.AgaricItems;
import com.dead_comedian.agaric.particle.AgaricParticles;
import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(AgaricMod.MOD_ID)
public class AgaricMod
{
    //
    public static final String MOD_ID = "agaric";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public AgaricMod(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);

        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);


        AgaricBlocks.register(modEventBus);
        AgaricItems.register(modEventBus);
        AgaricEntities.register(modEventBus);
        AgaricCreativeTab.register(modEventBus);
        AgaricParticles.register(modEventBus);

    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {

    }



    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {

    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            EntityRenderers.register(AgaricEntities.SPORDER.get(), SporderRenderer::new);
            EntityRenderers.register(AgaricEntities.AGARACHNID.get(), AgarachnidRenderer::new);

        }
    }
}
