package world.landfall.slipboot;

import com.flowpowered.math.vector.Vector3d;
import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.BlueMapMap;
import de.bluecolored.bluemap.api.BlueMapWorld;
import de.bluecolored.bluemap.api.gson.MarkerGson;
import de.bluecolored.bluemap.api.markers.ElementMarker;
import de.bluecolored.bluemap.api.markers.HtmlMarker;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import de.bluecolored.bluemap.api.markers.POIMarker;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.level.Level;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderedImageFactory;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

public class BlueMapIntegration {
    public static HashMap<String, MarkerSet> markerSetMap = new HashMap<String, MarkerSet>();
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

            for (ServerLevel x : levels) {
                var dimension = x.dimension().location().toString();
                var name = dimension;
                var marker = MarkerSet.builder().label(name + " Warps").build();
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
    }
    public static void addMarker(BlockPos pos, String name, int id, ResourceLocation dimension, boolean active) {
        System.out.println(dimension.toString() + " " + dimension.getPath());
        markerSetMap.get(dimension.toString()+"_warps").put(id+"", HtmlMarker.builder()
                .html(makeHTML(name.replaceAll("<[^>]*>",""), active))
                .label("test")
                .position(new Vector3d(pos.getX()+.5d, pos.getY()+.5d, (double)pos.getZ()+.5d))
                .build()

        );
    }
    public static void setName(int id, String name, ResourceLocation dimension) {
        setName(id, name, dimension, true);
    }
    public static void setName(int id, String name, ResourceLocation dimension, boolean active) {
        markerSetMap.get(dimension.toString()+"_warps").get(id+"").setLabel(name);
        if (markerSetMap.get(dimension.toString()+"_warps").get(id+"") instanceof HtmlMarker html)
            html.setHtml(makeHTML(name, active));
    }
    public static void removeMarker(int id, ResourceLocation dimension) {
        markerSetMap.get(dimension.toString()+"_warps").remove(id+"");
    }
}
