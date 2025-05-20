package com.landfall.slipboot.ui;

import com.landfall.slipboot.ModBlocks;
import com.landfall.slipboot.ModMenuTypes;
import com.landfall.slipboot.blocks.WarpBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class WarpMenu extends AbstractContainerMenu {
    private final Level level;
    private final BlockEntity entity;
    public WarpMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(containerId, playerInventory, playerInventory.player.level().getBlockEntity(buf.readBlockPos()));
    }

    public WarpMenu(int containerId, Inventory playerInventory, BlockEntity entity) {
        super(ModMenuTypes.WARP_MENU.get(), containerId);
        this.level = playerInventory.player.level();
        this.entity = entity;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        // TODO Auto-generated method stub
        return stillValid(ContainerLevelAccess.create(level, entity.getBlockPos()), player, ModBlocks.WARP.get());
    }
    
}
