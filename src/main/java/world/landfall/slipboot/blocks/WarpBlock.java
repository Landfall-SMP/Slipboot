package world.landfall.slipboot.blocks;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import world.landfall.slipboot.Slipboot;
import world.landfall.slipboot.WarpLocations;

import world.landfall.slipboot.Constants;

import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Method;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class WarpBlock extends RepairableBlock {
    private static final String[] randomNames = {"Terranova", "New Ides", "Glazov", "Cuidense", "Saludo", "Whimsy"};
    private static WarpLocations locationData = Slipboot.locationData;

    private static Method openWarpScreenMethod = null;
    private static boolean reflectionInitialized = false;
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
            level.setBlock(pos.above(), BuiltInRegistries.BLOCK.get(Constants.modResource(Constants.FAKE_TOP_BLOCK)).defaultBlockState(),Block.UPDATE_CLIENTS);
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
        if (level.getBlockState(pos.above()).is(BuiltInRegistries.BLOCK.get(Constants.modResource(Constants.FAKE_TOP_BLOCK)))) {
            level.setBlock(pos.above(), BuiltInRegistries.BLOCK.get(ResourceLocation.parse("minecraft:air")).defaultBlockState(), Block.UPDATE_CLIENTS);
        }
        Slipboot.LOGGER.info("Warp at {} removed.", pos.toString());
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        boolean destroyed = super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
        if (!destroyed) {
            // Likely destroyed in Survival, meaning it only changed states. Add code here for sound effects and bluemap handling
            if (locationData != null && !level.isClientSide()) {
                locationData.setActive(locationData.getId(pos), false);
            }
            if (level.getBlockState(pos.above()).is(BuiltInRegistries.BLOCK.get(Constants.modResource(Constants.FAKE_TOP_BLOCK)))) {
                level.setBlock(
                        pos.above(),
                        BuiltInRegistries.BLOCK.get(Constants.modResource(Constants.FAKE_TOP_BLOCK)).defaultBlockState().setValue(FakeTop.brokenState, FakeTop.BrokenState.BROKEN),
                        Block.UPDATE_CLIENTS
                );

            }
            Slipboot.LOGGER.info("Warp at {} broken.", pos.toString());
        }
        return destroyed;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        BrokenState brokenState = state.getValue(RepairableBlock.brokenState);
        
        // If the block is intact, open the GUI
        if (brokenState == BrokenState.INTACT) {
            if (level.isClientSide()) {
                Slipboot.LOGGER.info("useWithoutItem: Attempting to open warp screen for pos: " + pos);
                openClientScreen(pos, player);
            }
            return InteractionResult.SUCCESS;
        }
        
        return InteractionResult.PASS;
    }
    
    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        BrokenState brokenState = state.getValue(RepairableBlock.brokenState);
        
        // Handle name tag first
        if (brokenState == BrokenState.INTACT && stack.is(BuiltInRegistries.ITEM.get(ResourceLocation.parse("minecraft:name_tag")))) {
            if (locationData != null && !level.isClientSide()) {
                var customName = stack.getComponents().get(DataComponents.CUSTOM_NAME);
                if (customName != null) {
                    String newName = Constants.sanitizeName(customName.getString());
                    
                    if (!newName.isEmpty()) {
                        locationData.setName(locationData.getId(pos), newName);
                        if (!player.isCreative())
                            stack.shrink(1);
                        if (level.getBlockState(pos.above()).is(BuiltInRegistries.BLOCK.get(Constants.modResource(Constants.FAKE_TOP_BLOCK)))) {
                            BlockState fakeTopState = level.getBlockState(pos.above());
                            level.setBlock(pos.above(), fakeTopState.setValue(FakeTop.brokenState, FakeTop.BrokenState.INTACT), Block.UPDATE_CLIENTS);
                        }
                        Slipboot.LOGGER.info("Warp at {} renamed to: {}", pos, newName);
                    } else {
                        Slipboot.LOGGER.warn("Invalid warp name provided (empty after sanitization)");
                    }
                }
            }
            player.swing(hand);
            return ItemInteractionResult.CONSUME;
        }
        
        // Check parent class for repair functionality
        ItemInteractionResult result = super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        
        switch(result) {
            case ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION:
                // If the block is intact and we're not holding a special item, open the menu
                if (brokenState == BrokenState.INTACT) {
                    if (level.isClientSide()) {
                        Slipboot.LOGGER.info("Attempting to open warp screen for pos: " + pos);
                        openClientScreen(pos, player);
                    }
                    player.swing(hand);
                    return ItemInteractionResult.CONSUME;
                }
                break;
            case ItemInteractionResult.SUCCESS:
                if (!level.isClientSide())
                    locationData.setActive(locationData.getId(pos), true);
                if (level.getBlockState(pos.above()).is(BuiltInRegistries.BLOCK.get(Constants.modResource(Constants.FAKE_TOP_BLOCK)))) {
                    level.setBlock(
                            pos.above(),
                            BuiltInRegistries.BLOCK.get(Constants.modResource(Constants.FAKE_TOP_BLOCK)).defaultBlockState(),
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

    private static void openClientScreen(BlockPos pos, Player player) {
        // Initialize reflection cache on first use
        if (!reflectionInitialized) {
            try {
                // Cache reflection results for better performance
                Class<?> clientHelperClass = Class.forName("world.landfall.slipboot.client.ClientHelper");
                openWarpScreenMethod = clientHelperClass.getMethod("openWarpScreen", BlockPos.class, Player.class);
                reflectionInitialized = true;
                Slipboot.LOGGER.debug("ClientHelper reflection cache initialized");
            } catch (ClassNotFoundException e) {
                Slipboot.LOGGER.error("ClientHelper class not found", e);
                reflectionInitialized = true; // Don't retry
                return;
            } catch (NoSuchMethodException e) {
                Slipboot.LOGGER.error("openWarpScreen method not found", e);
                reflectionInitialized = true; // Don't retry
                return;
            }
        }
        
        // Use cached reflection to open screen
        if (openWarpScreenMethod != null) {
            try {
                openWarpScreenMethod.invoke(null, pos, player);
                Slipboot.LOGGER.debug("Successfully opened warp screen at {}", pos);
            } catch (Exception e) {
                Slipboot.LOGGER.error("Failed to open warp screen: " + e.getMessage(), e);
            }
        }
    }


}
