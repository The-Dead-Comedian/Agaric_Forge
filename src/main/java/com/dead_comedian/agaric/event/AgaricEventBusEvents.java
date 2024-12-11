package com.dead_comedian.agaric.event;

import com.dead_comedian.agaric.AgaricMod;
import com.dead_comedian.agaric.entity.AgaricEntities;
import com.dead_comedian.agaric.entity.SporderEntity;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AgaricMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)

public class AgaricEventBusEvents {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(AgaricEntities.SPORDER.get(), SporderEntity.createAttributes().build());
    }
}
