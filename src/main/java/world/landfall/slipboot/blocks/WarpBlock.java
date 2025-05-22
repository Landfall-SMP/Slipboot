package world.landfall.slipboot.blocks;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleMenuProvider;
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
import world.landfall.slipboot.ui.WarpScreen;

public class WarpBlock extends RepairableBlock {
    public WarpBlock(Properties properties) {
        super(properties
                .destroyTime(4f),
        "minecraft:redstone");
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        boolean destroyed = super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
        if (destroyed) {
            // Likely destroyed in Creative, which bypasses the unbreakable code
        } else {
            // Likely destroyed in Survival, meaning it only changed states. Add code here for sound effects and bluemap handling
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
                    player.swing(hand);
                    if (minecraft.player != null)
                        minecraft.setScreen(new WarpScreen(pos, player));
                    return ItemInteractionResult.CONSUME;
                }
            case ItemInteractionResult.SUCCESS:
                return ItemInteractionResult.CONSUME;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WarpBlockEntity(pos, state);
    }

}
