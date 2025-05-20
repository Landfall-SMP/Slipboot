package com.landfall.slipboot.blocks;

import com.landfall.slipboot.SlipBoot;
import net.minecraft.core.BlockPos;
// import de.bluecolored.bluemap.api.BlueMapAPI;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

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
        BrokenStateEnum brokenState = state.getValue(RepairableBlock.brokenState);
        InteractionResult result = super.use(state, level, pos, player, hand, hit);
        if (result == InteractionResult.PASS && !level.isClientSide() && brokenState == BrokenStateEnum.INTACT) {
            if (level.getBlockEntity(pos) instanceof WarpBlockEntity entity) {
                NetworkHooks.openScreen((ServerPlayer)player, (WarpBlockEntity)entity, pos);
                player.swing(hand);
                return InteractionResult.CONSUME;
            }
        } else if (result == InteractionResult.PASS && brokenState == BrokenStateEnum.INTACT)
            return InteractionResult.CONSUME;
        else if (result == InteractionResult.SUCCESS)
            return InteractionResult.CONSUME;
        return InteractionResult.PASS;
    }
    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        // TODO Auto-generated method stub
        return new WarpBlockEntity(pos, state);
    }
    @Mod.EventBusSubscriber(modid = SlipBoot.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.DEDICATED_SERVER)
    public class ServerModEvents {
        public static void onPlace(PlayerEvent event) {
            
        }
        
    }
}
