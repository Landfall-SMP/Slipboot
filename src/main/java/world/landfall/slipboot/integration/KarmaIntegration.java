package world.landfall.slipboot.integration;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import world.landfall.landfallessentials.api.karma.KarmaAPI;
import world.landfall.slipboot.Slipboot;

public class KarmaIntegration {
    private static class Cost {
        public static final double PER_BLOCK_DISTANCE = 1;
        public static final double CROSS_DIMENSION_FEE = 200;
    }

    private static KarmaAPI api = KarmaAPI.getInstance();

    public static double calculateCost(Vec3 fromPos, Vec3 toPos, ResourceLocation fromDimension, ResourceLocation toDimension) {
        double distance = fromPos.distanceTo(toPos);
        boolean crossDimension = !fromDimension.equals(toDimension);
        double distanceCost = distance * Cost.PER_BLOCK_DISTANCE;
        return (crossDimension ? Cost.CROSS_DIMENSION_FEE : distanceCost * Cost.PER_BLOCK_DISTANCE);
    }

    public static boolean chargePlayer(Player player, Vec3 fromPos, Vec3 toPos, ResourceLocation fromDimension, ResourceLocation toDimension) {
        double cost = calculateCost(fromPos, toPos, fromDimension, toDimension);
        double balance = api.getKarma(player);
        if (cost <= balance) {
            api.removeKarma(player, (int)cost, "Warp fee");
        }
        Slipboot.LOGGER.info("Cost: " + cost + " Balance: " + balance);
        return cost <= balance;
    }

}
