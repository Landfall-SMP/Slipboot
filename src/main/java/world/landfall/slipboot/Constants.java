package world.landfall.slipboot;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;

/**
 * Central location for all constants and utility methods used throughout the mod
 */
public final class Constants {
    private Constants() {} // Prevent instantiation

    // Limits
    public static final int MAX_WARP_NAME_LENGTH = 50;
    public static final int MAX_PACKET_NAME_LENGTH = 100;
    
    // World boundaries
    public static final double MAX_WORLD_COORDINATE = 30000000; // Minecraft world border default
    public static final double MIN_Y_COORDINATE = -64;
    public static final double MAX_Y_COORDINATE = 320;
    
    // Teleportation costs between dimensions
    public static final String[] DIMENSIONS = {
        "minecraft:overworld", 
        "minecraft:the_nether", 
        "minecraft:the_end"
    };
    
    public static final int[][] TELEPORT_COSTS = {
        {0, 1, 2},  // From Overworld
        {1, 0, 3},  // From Nether
        {2, 3, 0},  // From End
    };
    
    // Block registry names
    public static final String FAKE_TOP_BLOCK = "fake_top";
    
    // Sanitization pattern for names
    public static final String NAME_SANITIZATION_PATTERN = "[<>\"'&]";
    
    // Permissions
    public static final int DEFAULT_PLAYER_PERMISSION_LEVEL = 0;
    
    // Utility methods
    
    /**
     * Create a ResourceLocation for this mod
     */
    public static ResourceLocation modResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(Slipboot.MODID, path);
    }
    
    /**
     * Safely limit string length
     */
    public static String limitLength(String str, int maxLength) {
        if (str == null) return "";
        return str.substring(0, Math.min(str.length(), maxLength));
    }
    
    /**
     * Sanitize a warp name
     */
    public static String sanitizeName(String name) {
        if (name == null || name.isEmpty()) return "";
        return limitLength(name.replaceAll(NAME_SANITIZATION_PATTERN, ""), MAX_WARP_NAME_LENGTH);
    }

    /**
     * Validate coordinates are within world boundaries
     */
    public static boolean isValidPosition(BlockPos pos) {
        return Math.abs(pos.getX()) <= MAX_WORLD_COORDINATE &&
               Math.abs(pos.getZ()) <= MAX_WORLD_COORDINATE &&
               pos.getY() >= MIN_Y_COORDINATE &&
               pos.getY() <= MAX_Y_COORDINATE;
    }

    /**
     * Get teleportation cost between dimensions
     */
    public static int getTeleportCost(String fromDimension, String toDimension) {
        int fromIndex = -1, toIndex = -1;
        for (int i = 0; i < DIMENSIONS.length; i++) {
            if (DIMENSIONS[i].equals(fromDimension)) fromIndex = i;
            if (DIMENSIONS[i].equals(toDimension)) toIndex = i;
        }
        if (fromIndex >= 0 && toIndex >= 0) {
            return TELEPORT_COSTS[fromIndex][toIndex];
        }
        return 0; // Default cost for unknown dimensions
    }
}