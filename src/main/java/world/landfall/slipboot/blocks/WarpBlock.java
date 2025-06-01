package world.landfall.slipboot.blocks;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import world.landfall.slipboot.Slipboot;
import world.landfall.slipboot.WarpLocations;
import world.landfall.slipboot.ui.WarpScreen;

public class WarpBlock extends RepairableBlock {
    private static WarpLocations locationData;
    public WarpBlock(Properties properties) {
        super(properties
                .destroyTime(4f),
        "minecraft:redstone");
    }
    public static void setLocationData(WarpLocations locationData) {
        WarpBlock.locationData = locationData;
    }
    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide())
            locationData.addLocation("", pos, state.getValue(brokenState) == BrokenState.INTACT, level.dimension().location().toString());


    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        boolean destroyed = super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
        if (destroyed) {
            // Likely destroyed in Creative, which bypasses the unbreakable code. Add code here for sound effects and bluemap handling
            if (locationData != null && !level.isClientSide()) {
                locationData.removeLocation(locationData.getId(pos));
            }
        } else {
            // Likely destroyed in Survival, meaning it only changed states. Add code here for sound effects and bluemap handling
            if (locationData != null && !level.isClientSide()) {
                locationData.setActive(locationData.getId(pos), false);
            }
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
                            stack.shrink(1);

                        }

                    } else if (minecraft.player != null)
                        minecraft.setScreen(new WarpScreen(pos, player));
                    player.swing(hand);
                    return ItemInteractionResult.CONSUME;
                }
                break;
            case ItemInteractionResult.SUCCESS:
                if (!level.isClientSide())
                    locationData.setActive(locationData.getId(pos), true);
                return ItemInteractionResult.CONSUME;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WarpBlockEntity(pos, state);
    }

}
