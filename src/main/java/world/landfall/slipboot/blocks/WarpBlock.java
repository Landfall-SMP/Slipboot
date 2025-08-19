package world.landfall.slipboot.blocks;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import world.landfall.slipboot.ModBlocks;
import world.landfall.slipboot.Slipboot;
import world.landfall.slipboot.WarpLocations;
import world.landfall.slipboot.ui.WarpScreen;

import java.util.random.RandomGenerator;

public class WarpBlock extends RepairableBlock {
    private static final String[] randomNames = {"This place", "That place", "That other place", "America", "Antarctica"};
    private static WarpLocations locationData = Slipboot.locationData;
    public static final VoxelShape SHAPE = Block.box(1, 0, 1, 15, 32, 15);
    public static final VoxelShape BROKEN_SHAPE = Block.box(1, 0, 1, 15, 21, 15);
    public WarpBlock(Properties properties) {
        super(properties
                .noOcclusion()
                .dynamicShape()
                .explosionResistance(Float.POSITIVE_INFINITY)
                .destroyTime(4f),
        "minecraft:redstone");
    }
    public static void setLocationData(WarpLocations locations) {
        locationData = locations;
    }
    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (!level.isClientSide())
            locationData.addLocation(randomNames[(int)(Math.random() * randomNames.length)], pos, state.getValue(brokenState) == BrokenState.INTACT, level.dimension().location().toString());


        if (level.getBlockState(pos.above()).is(BuiltInRegistries.BLOCK.get(ResourceLocation.parse("minecraft:air")))) {
            level.setBlock(pos.above(), BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(Slipboot.MODID, "fake_top")).defaultBlockState(),Block.UPDATE_CLIENTS);
        }
        Slipboot.LOGGER.info("Warp at " + pos.toString() + " created.");
    }
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        /*level.addParticle(new ParticleOptions() {
            @Override
            public ParticleType<?> getType() {
                return ParticleTypes.FLAME;
            }
        }, pos.getX(), pos.getY(), pos.getZ(), 0., 0., 0.);*/
    }

    @Override
    public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
        super.destroy(level, pos, state);
        // Likely destroyed in Creative, which bypasses the unbreakable code. Add code here for sound effects and bluemap handling
        if (locationData != null && !level.isClientSide()) {
            locationData.removeLocation(locationData.getId(pos));
        }
        if (level.getBlockState(pos.above()).is(BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(Slipboot.MODID, "fake_top")))) {
            level.setBlock(pos.above(), BuiltInRegistries.BLOCK.get(ResourceLocation.parse("minecraft:air")).defaultBlockState(), Block.UPDATE_CLIENTS);
        }
        Slipboot.LOGGER.info("Warp at " + pos.toString() + " removed.");
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        boolean destroyed = super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
        if (!destroyed) {
            // Likely destroyed in Survival, meaning it only changed states. Add code here for sound effects and bluemap handling
            if (locationData != null && !level.isClientSide()) {
                locationData.setActive(locationData.getId(pos), false);
            }
            if (level.getBlockState(pos.above()).is(BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(Slipboot.MODID, "fake_top")))) {
                level.setBlock(
                        pos.above(),
                        BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(Slipboot.MODID, "fake_top")).defaultBlockState().setValue(FakeTop.brokenState, FakeTop.BrokenState.BROKEN),
                        Block.UPDATE_CLIENTS
                );

            }
            Slipboot.LOGGER.info("Warp at " + pos.toString() + " broken.");
        }
        return destroyed;
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        BrokenState brokenState = state.getValue(RepairableBlock.brokenState);
        ItemInteractionResult result = super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        Minecraft minecraft = Minecraft.getInstance();
        switch(result) {
            case ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION:
                if (brokenState == BrokenState.INTACT) {
                    if (stack.is(BuiltInRegistries.ITEM.get(ResourceLocation.parse("minecraft:name_tag")))) {
                        if (locationData != null && !level.isClientSide()) {
                            locationData.setName(locationData.getId(pos), stack.getComponents().get(DataComponents.CUSTOM_NAME).getString());
                            if (!player.isCreative())
                                stack.shrink(1);
                            if (level.getBlockState(pos.above()).is(BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(Slipboot.MODID, "fake_top")))) {

                                level.getBlockState(pos.above()).setValue(FakeTop.brokenState, FakeTop.BrokenState.INTACT);

                            }
                            Slipboot.LOGGER.info("Warp at " + pos.toString() + " repaired.");
                        }

                    } else if (minecraft.player != null && level.isClientSide())
                        minecraft.setScreen(new WarpScreen(pos, player));
                    player.swing(hand);
                    return ItemInteractionResult.CONSUME;

                }
                break;
            case ItemInteractionResult.SUCCESS:
                if (!level.isClientSide())
                    locationData.setActive(locationData.getId(pos), true);
                System.out.println("One");
                if (level.getBlockState(pos.above()).is(BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(Slipboot.MODID, "fake_top")))) {
                    System.out.println("Two");
                    level.setBlock(
                            pos.above(),
                            BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(Slipboot.MODID, "fake_top")).defaultBlockState(),
                            Block.UPDATE_CLIENTS
                    );

                }
                return ItemInteractionResult.CONSUME;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WarpBlockEntity(pos, state);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        BrokenState brokenState = state.getValue(RepairableBlock.brokenState);

        return brokenState == BrokenState.BROKEN ? BROKEN_SHAPE : SHAPE;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }


}
