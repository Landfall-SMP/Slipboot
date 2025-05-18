package com.landfall.slipboot.blocks;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
public class RepairableBlock extends BaseEntityBlock implements EntityBlock {

    
    enum BrokenStateEnum implements StringRepresentable {
        INTACT,
        BROKEN;

        @Override
        public String getSerializedName() {
            return switch(this) {
                case INTACT:
                    yield "intact";
                case BROKEN:
                    yield "broken";
            }; 
        }
        
    }

    String repairItem;
    static final EnumProperty<BrokenStateEnum> brokenState = EnumProperty.create("broken_state", BrokenStateEnum.class);
    public RepairableBlock(Properties properties, String repairItem) {
        super(properties
            .destroyTime(1)
            
        );
        
        this.repairItem = repairItem;
        this.registerDefaultState(this.stateDefinition.any().setValue(brokenState, BrokenStateEnum.INTACT));
    }
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        switch(state.getValue(brokenState)) {
            case BROKEN:
                var item = player.getItemInHand(hand);
                if (item.is(ForgeRegistries.ITEMS.getValue(ResourceLocation.parse(repairItem)))) {
                    level.setBlock(pos, state.setValue(brokenState, BrokenStateEnum.INTACT), UPDATE_ALL);
                    item.shrink(1);
                    return InteractionResult.SUCCESS;
                }
                return InteractionResult.PASS;
            default:
                return InteractionResult.PASS;
        }
    }
    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        builder.add(brokenState);
        super.createBlockStateDefinition(builder);
    }
    // @Override
    // public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
    //     // TODO Auto-generated method stub
    //     if (state.getValue(brokenState) == BrokenStateEnum.INTACT)
    //         level.setBlock(pos, state.setValue(brokenState, BrokenStateEnum.BROKEN), 3);
    // }
    // @Override
    // public void playerDestroy(Level p_49827_, Player p_49828_, BlockPos p_49829_, BlockState p_49830_,
    //         @Nullable BlockEntity p_49831_, ItemStack p_49832_) {
        
    // }
    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest,
            FluidState fluid) {
        if (!player.isCreative()) {
            
            this.spawnDestroyParticles(level, player, pos, state);
            level.setBlock(pos, state.setValue(brokenState, BrokenStateEnum.BROKEN), UPDATE_ALL);
            level.sendBlockUpdated(pos, state, state.setValue(brokenState, BrokenStateEnum.BROKEN), Block.UPDATE_ALL);
        } else return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
        player.swing(InteractionHand.MAIN_HAND);
        level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 1.0F, 1.0F);
        return false;
    }
    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        // TODO Auto-generated method stub
        return new RepairableBlockEntity(pos, state);
    }
}
