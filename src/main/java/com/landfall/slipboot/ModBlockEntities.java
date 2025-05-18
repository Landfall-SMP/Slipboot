package com.landfall.slipboot;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import com.landfall.slipboot.blocks.RepairableBlockEntity;
import com.landfall.slipboot.blocks.WarpBlockEntity;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCKENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, SlipBoot.MODID);
    public static final RegistryObject<BlockEntityType<RepairableBlockEntity>> REPAIRABLE_BLOCK_ENTITY = BLOCKENTITIES.register("repairable_block",() -> BlockEntityType.Builder.of(RepairableBlockEntity::new, ModBlocks.BREAKABLE_BLOCK.get()).build(null));
    public static final RegistryObject<BlockEntityType<WarpBlockEntity>> WARP_ENTITY = BLOCKENTITIES.register("warp",() -> BlockEntityType.Builder.of(WarpBlockEntity::new, ModBlocks.WARP.get()).build(null));
    public static void register(IEventBus iEventBus) {
        BLOCKENTITIES.register(iEventBus);
    }
}
