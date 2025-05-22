package world.landfall.slipboot.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import world.landfall.slipboot.ModBlockEntities;

public class RepairableBlockEntity extends BlockEntity {

    public RepairableBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntities.REPAIRABLE_BLOCK_ENTITY.get(), pos, state);
    }
    public RepairableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }
}
