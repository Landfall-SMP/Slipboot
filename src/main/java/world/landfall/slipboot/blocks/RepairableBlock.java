package world.landfall.slipboot.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.Nullable;

import static world.landfall.slipboot.blocks.RepairableBlock.BrokenState.BROKEN;
import static world.landfall.slipboot.blocks.RepairableBlock.BrokenState.INTACT;

public class RepairableBlock extends BaseEntityBlock implements EntityBlock {
    enum BrokenState implements StringRepresentable {
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

    static final EnumProperty<BrokenState> brokenState = EnumProperty.create("broken_state", BrokenState.class);
    String repairItem;
    public RepairableBlock(Properties properties, String repairItem) {
        super(properties);
        this.repairItem = repairItem;
        this.registerDefaultState(this.getStateDefinition().any().setValue(brokenState, INTACT));
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {

        return switch(state.getValue(brokenState)) {
            case BROKEN:
                if (stack.is(BuiltInRegistries.ITEM.get(ResourceLocation.parse(repairItem)))) {
                    level.setBlock(pos, state.setValue(brokenState, INTACT), UPDATE_ALL);
                    stack.shrink(1);
                    yield ItemInteractionResult.SUCCESS;
                }
                yield ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            case INTACT:
                yield ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(brokenState);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        if (!player.isCreative()) {
            this.spawnDestroyParticles(level, player, pos, state);
            level.setBlock(pos, state.setValue(brokenState, BROKEN), UPDATE_ALL);
        } else return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
        player.swing(InteractionHand.MAIN_HAND);
        level.playSound(player, pos, SoundEvents.AMETHYST_BLOCK_BREAK, SoundSource.BLOCKS);
        return false;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return null;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RepairableBlockEntity(pos, state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}
