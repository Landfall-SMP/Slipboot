package com.landfall.slipboot;
import java.util.Map;

import com.landfall.slipboot.blocks.RepairableBlock;
import com.landfall.slipboot.blocks.WarpBlock;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, SlipBoot.MODID);
    // Creates a new Block with the id "examplemod:example_block", combining the namespace and path
    public static final RegistryObject<Block> BREAKABLE_BLOCK = BLOCKS.register("repairable_block", () -> new RepairableBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE), ModItems.REPAIR_ITEM.getId().toString()));
    public static final RegistryObject<Block> WARP = BLOCKS.register("warp", () -> new WarpBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));
    
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
