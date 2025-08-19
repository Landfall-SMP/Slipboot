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
import net.minecraft.server.packs.resources.Resource;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderedImageFactory;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class BlueMapIntegration {
    public static MarkerSet markerSet = MarkerSet.builder()
            .label("Warps")
            .build();
    private static String makeHTML(String name, boolean active) {
        return String.format("""
                <div class="bm-marker-player" distance-data="near">
                    <img src="assets/%s.png" alt="playerhead" draggable="true">
                    <div class="bm-player-name">%s</div>
                </div>
                """, active ? "warp_intact" : "warp_broken", name);
    }
    public static void addMarker(BlockPos pos, String name, int id) {
        addMarker(pos, name, id, true);
    }
    public static void addMarker(BlockPos pos, String name, int id, boolean active) {
        markerSet.put(id+"", HtmlMarker.builder()
                .html(makeHTML(name.replaceAll("<[^>]*>",""), active))
                        .label("test")
                .position(new Vector3d(pos.getX()+.5d, pos.getY()+.5d, (double)pos.getZ()+.5d))
                .build()
        );
    }
    public static void init() {
        ;
        BlueMapAPI.onEnable(api -> {
            for (BlueMapMap x : api.getMaps()) {
                x.getMarkerSets().put("warp_locations", markerSet);

            }
        });
    }
    public static void setName(int id, String name) {
        setName(id, name, true);
    }
    public static void setName(int id, String name, boolean active) {
        markerSet.get(id+"").setLabel(name);
        if (markerSet.get(id+"") instanceof HtmlMarker html)
            html.setHtml(makeHTML(name, active));
    }
    public static void removeMarker(int id) {
        markerSet.remove(id+"");
    }
}
