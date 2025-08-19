package world.landfall.slipboot.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import world.landfall.slipboot.Slipboot;
import world.landfall.slipboot.WarpLocations;


public class FakeTop extends Block {
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
    public static final VoxelShape SHAPE = Block.box(1, -16, 1, 15, 32 - 16, 15);
    public static final VoxelShape BROKEN_SHAPE = Block.box(1, -16, 1, 15, 21 - 16, 15);
    static final EnumProperty<FakeTop.BrokenState> brokenState = EnumProperty.create("fake_broken_state", FakeTop.BrokenState.class);
    public FakeTop(Properties properties) {
        super(
                properties
                        .pushReaction(PushReaction.BLOCK)
                        .explosionResistance(Float.POSITIVE_INFINITY)
                        .noOcclusion()
                        .destroyTime(4f)
        );
        this.registerDefaultState(this.getStateDefinition().any().setValue(brokenState, BrokenState.INTACT));
    }
    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        var warp = level.getBlockState(pos.below());
        var destroyed = warp.onDestroyedByPlayer(level, pos.below(), player, willHarvest, fluid);
        System.out.println(destroyed);
        if (destroyed)
            warp.getBlock().destroy(level, pos.below(), warp);
        return player.isCreative();
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        return level.getBlockState(pos.below()).useItemOn(stack, level, player, hand, hitResult.withPosition(pos.below()));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(brokenState);
        super.createBlockStateDefinition(builder);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        FakeTop.BrokenState brokenState = state.getValue(FakeTop.brokenState);

        return brokenState == FakeTop.BrokenState.BROKEN ? BROKEN_SHAPE : SHAPE;
    }
    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }
}
