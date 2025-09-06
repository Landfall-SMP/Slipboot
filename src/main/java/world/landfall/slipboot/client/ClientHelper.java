package world.landfall.slipboot.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import world.landfall.slipboot.Slipboot;
import world.landfall.slipboot.ui.WarpScreen;

@OnlyIn(Dist.CLIENT)
public class ClientHelper {
    public static void openWarpScreen(BlockPos pos, Player player) {
        try {
            Slipboot.LOGGER.info("ClientHelper.openWarpScreen called for pos: " + pos);
            WarpScreen screen = new WarpScreen(pos, player);
            Minecraft.getInstance().setScreen(screen);
            Slipboot.LOGGER.info("WarpScreen set successfully");
        } catch (Exception e) {
            Slipboot.LOGGER.error("Error in ClientHelper.openWarpScreen", e);
        }
    }
}