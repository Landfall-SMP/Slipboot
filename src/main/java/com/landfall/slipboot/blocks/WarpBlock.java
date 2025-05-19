package com.landfall.slipboot.blocks;

import javax.annotation.Nullable;

import com.landfall.slipboot.ui.WarpMenu;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;

public class WarpBlock extends RepairableBlock {
    private BlockPos pos;
    public WarpBlock(Properties properties) {
        super(
            properties
                .destroyTime(4f), 
            "minecraft:redstone");
        
    }
    
    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest,
            FluidState fluid) {
        boolean destroyed = super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
        if (destroyed) {

        }
        return destroyed;
    }
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
            BlockHitResult hit) {
        // TODO Auto-generated method stub
        this.pos = pos;
        InteractionResult result = super.use(state, level, pos, player, hand, hit);
        if (result == InteractionResult.PASS && !level.isClientSide()) {
            if (level.getBlockEntity(pos) instanceof WarpBlockEntity entity)
            ((ServerPlayer)player).openMenu(new SimpleMenuProvider(entity, getName()));
            return InteractionResult.SUCCESS;
        } else if (result == InteractionResult.PASS) {
            return InteractionResult.SUCCESS;
        } 
        
        else if (result == InteractionResult.SUCCESS) {

        }
        return result;
    }
    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        // TODO Auto-generated method stub
        return new WarpBlockEntity(pos, state);
    }
}
