package world.landfall.slipboot;

import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.BlueMapMap;
import de.bluecolored.bluemap.api.BlueMapWorld;
import de.bluecolored.bluemap.api.gson.MarkerGson;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import de.bluecolored.bluemap.api.markers.POIMarker;
import net.minecraft.core.BlockPos;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class BlueMapIntegration {
    public static MarkerSet markerSet = MarkerSet.builder()
            .label("Warps")
            .build();
    public static void addMarker(BlockPos pos, String name, int id) {
        markerSet.put(id+"", POIMarker.builder().label(name).position(pos.getX(), pos.getY(), (double)pos.getZ()).build());
    }
    public static void saveMarkers() {
        try (FileWriter writer = new FileWriter("marker-file.json")) {
            MarkerGson.INSTANCE.toJson(markerSet, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void loadMarkers() {
        try (FileReader reader = new FileReader("marker-file.json")) {
            markerSet = MarkerGson.INSTANCE.fromJson(reader, MarkerSet.class);
        } catch (IOException ex) {
            // handle io-exception
            ex.printStackTrace();
        }
        BlueMapAPI.getInstance().ifPresent(api -> {
            for (BlueMapMap x : api.getMaps()) {
                x.getMarkerSets().put("warp_locations", markerSet);
            }
        });
    }
    public static void setname(int id, String name) {
        markerSet.get(id+"").setLabel(name);
    }
    public static void removeMarker(int id) {
        markerSet.remove(id+"");
    }
}
