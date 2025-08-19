package world.landfall.slipboot.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import world.landfall.slipboot.ModBlockEntities;

public class WarpBlockEntity extends BlockEntity {
    public WarpBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntities.WARP_BLOCK_ENTITY.get(), pos, state);
    }
    public WarpBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
}
