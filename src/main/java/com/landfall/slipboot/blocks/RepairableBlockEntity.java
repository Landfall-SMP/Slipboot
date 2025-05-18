package com.landfall.slipboot.blocks;

import com.landfall.slipboot.ModBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class RepairableBlockEntity extends BlockEntity {
    public RepairableBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntities.REPAIRABLE_BLOCK_ENTITY.get(), pos, state);
    }
    public RepairableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        //TODO Auto-generated constructor stub
    }
    
}
