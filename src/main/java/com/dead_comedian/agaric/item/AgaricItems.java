package com.dead_comedian.agaric.item;

import com.dead_comedian.agaric.AgaricMod;
import com.dead_comedian.agaric.entity.AgaricEntities;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AgaricItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, AgaricMod.MOD_ID);



    public static final RegistryObject<Item> ROTTEN_FLESH_ON_A_STICK = ITEMS.register("rotten_flesh_on_a_stick",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> SPORDER_SPAWN_EGG = ITEMS.register("sporder_spawn_egg",
            () -> new ForgeSpawnEggItem(AgaricEntities.SPORDER,  0xe0d3ba,0x42964a, new Item.Properties()));

    public static final RegistryObject<Item> AGARACHNID_SPAWN_EGG = ITEMS.register("agarachnid_spawn_egg",
            () -> new ForgeSpawnEggItem(AgaricEntities.AGARACHNID,  0xe0d3ba,0x42964a, new Item.Properties()));


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
