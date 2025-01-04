package com.dead_comedian.agaric.entity;

import com.dead_comedian.agaric.AgaricMod;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AgaricEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, AgaricMod.MOD_ID);

    public static final RegistryObject<EntityType<SporderEntity>> SPORDER =
            ENTITY_TYPES.register("sporder", () -> EntityType.Builder.of(SporderEntity::new, MobCategory.CREATURE)
                    .sized(1, 1).build("sporder"));


    public static final RegistryObject<EntityType<AgarachnidEntity>> AGARACHNID =
            ENTITY_TYPES.register("agarachnid", () -> EntityType.Builder.of(AgarachnidEntity::new, MobCategory.CREATURE)
                    .sized(3, 3.5F).build("agarachnid"));


    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
