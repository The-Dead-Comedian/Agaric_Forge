package com.dead_comedian.agaric.block;

import com.dead_comedian.agaric.AgaricMod;
import com.dead_comedian.agaric.item.AgaricItems;
import net.minecraft.core.BlockPos;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class AgaricBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, AgaricMod.MOD_ID);




    public static final RegistryObject<Block> GREEN_MUSHROOM_BLOCK = registerBlock("green_mushroom_block",
            () -> new GreenMushroomBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GREEN)
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(0.2F)
                    .sound(SoundType.WOOD).ignitedByLava()));

    public static final RegistryObject<Block> GREEN_MUSHROOM = registerBlock("green_mushroom",
            () -> new MushroomBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN).noCollission().noOcclusion().randomTicks().instabreak().sound(SoundType.GRASS).lightLevel((p_50892_) -> {
                return 1;
            }).hasPostProcess(AgaricBlocks::always).pushReaction(PushReaction.DESTROY), TreeFeatures.HUGE_BROWN_MUSHROOM));




    private static boolean always(BlockState p_50775_, BlockGetter p_50776_, BlockPos p_50777_) {
        return true;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block){
        return AgaricItems.ITEMS.register(name, () -> new BlockItem(block.get(),new Item.Properties()));
    }

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }


    public static void register(IEventBus eventBus){
        BLOCKS.register(eventBus);
    }
}
