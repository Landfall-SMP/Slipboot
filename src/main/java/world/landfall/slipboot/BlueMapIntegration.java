package world.landfall.slipboot;

import com.flowpowered.math.vector.Vector3d;
import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.BlueMapMap;
import de.bluecolored.bluemap.api.markers.HtmlMarker;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;

import java.util.HashMap;

public class BlueMapIntegration {
    private static boolean bluemapAvailable = false;
    public static HashMap<String, MarkerSet> markerSetMap = new HashMap<>();
    
    static {
        try {
            Class.forName("de.bluecolored.bluemap.api.BlueMapAPI");
            bluemapAvailable = true;
            Slipboot.LOGGER.info("BlueMap API found, integration enabled");
        } catch (ClassNotFoundException e) {
            Slipboot.LOGGER.info("BlueMap API not found, integration disabled");
        }
    }
//    public static MarkerSet markerSet = MarkerSet.builder()
//            .label("Warps")
//            .build();
    public BlueMapIntegration() {
    }

    private static String makeHTML(String name, boolean active) {
        return String.format("""
                <div class="bm-marker-player" distance-data="near">
                    <img src="assets/%s.png" alt="playerhead" draggable="true">
                    <div class="bm-player-name">%s</div>
                </div>
                """, active ? "warp_intact" : "warp_broken", name);
    }
    public static void addMarker(BlockPos pos, String name, int id, ResourceLocation dimension) {
        addMarker(pos, name, id, dimension, true);
    }
    public static void init(Iterable<ServerLevel> levels) {
        if (!bluemapAvailable) {
            Slipboot.LOGGER.info("Skipping BlueMap initialization - API not available");
            return;
        }
        
        try {
            for (ServerLevel x : levels) {
                var dimension = x.dimension().location().toString();
                var marker = MarkerSet.builder().label(dimension + " Warps").build();
                markerSetMap.put(dimension + "_warps", marker);
            }
            System.out.println(markerSetMap.keySet());
            BlueMapAPI.onEnable(api -> {
                for (BlueMapMap x : api.getWorlds().iterator().next().getMaps()) {
                    for (String y : markerSetMap.keySet()) {
                        System.out.println(x.getWorld().getId());
                        if ((x.getWorld().getId().split("#")[1] + "_warps").equals(y))
                            x.getMarkerSets().put(y, markerSetMap.get(y));
                    }
                }
            });
        } catch (Exception e) {
            Slipboot.LOGGER.error("Failed to initialize BlueMap integration", e);
        }
    }
    public static void addMarker(BlockPos pos, String name, int id, ResourceLocation dimension, boolean active) {
        if (!bluemapAvailable) return;
        
        try {
            System.out.println(dimension.toString() + " " + dimension.getPath());
        markerSetMap.get(dimension +"_warps").put(id+"", HtmlMarker.builder()
                .html(makeHTML(name.replaceAll("<[^>]*>",""), active))
                .label("test")
                .position(new Vector3d(pos.getX()+.5d, pos.getY()+.5d, (double)pos.getZ()+.5d))
                .build()

        );
        } catch (Exception e) {
            Slipboot.LOGGER.error("Failed to add BlueMap marker", e);
        }
    }
    public static void removeMarker(int id, ResourceLocation dimension) {
        if (!bluemapAvailable) return;
        
        try {
            markerSetMap.get(dimension.toString()+"_warps").remove(id+"");
        } catch (Exception e) {
            Slipboot.LOGGER.error("Failed to remove BlueMap marker", e);
        }
    }
}
