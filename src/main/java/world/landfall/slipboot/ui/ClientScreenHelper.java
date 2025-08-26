package world.landfall.slipboot.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

public class ClientScreenHelper {

    public static void openWarpScreen(BlockPos pos, Player player) {
        Minecraft.getInstance().setScreen(new WarpScreen(pos, player));
    }
}
