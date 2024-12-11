package com.dead_comedian.agaric.item;


import com.dead_comedian.agaric.AgaricMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class AgaricCreativeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AgaricMod.MOD_ID);

    public static final RegistryObject<CreativeModeTab> TUTORIAL_TAB = CREATIVE_MODE_TABS.register("tutorial_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(AgaricItems.ROTTEN_FLESH_ON_A_STICK.get()))
                    .title(Component.translatable("creativetab.agaric_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(AgaricItems.ROTTEN_FLESH_ON_A_STICK.get());






                    })
                    .build());


    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}