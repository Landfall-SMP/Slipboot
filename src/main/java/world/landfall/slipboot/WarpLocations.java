package world.landfall.slipboot;

import de.bluecolored.bluemap.api.gson.MarkerGson;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Position;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;
import de.bluecolored.bluemap.api.BlueMapAPI;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class WarpLocations extends SavedData {

    public static final String[] dimensions = {"minecraft:overworld", "minecraft:the_nether", "minecraft:the_end"};
    public static final int[][] cost = {
            {0, 1, 2},
            {1, 0, 3},
            {2, 3, 0},
    };

    public static class WarpLocation {
        public String name;
        public int id;
        public BlockPos pos;
        public boolean active;
        public String level;
        public WarpLocation(String name, BlockPos pos, int id, boolean active, String level) {
            this.name = name;
            this.pos = pos;
            this.active = active;
            this.id = id;
            this.level = level;
        }
        public boolean equals(WarpLocation e) {
            return pos.equals(e.pos);

        }
    }
    private final HashMap<Integer, WarpLocation> locations;
    public WarpLocations() {
        locations = new HashMap<Integer, WarpLocation>();
    }
    public static WarpLocations create() {
        return new WarpLocations();
    }

    public static int getCost(String a, String b) {
        for (int i = 0; i < dimensions.length; i++)
            if (dimensions[i].equals(a))
                for (int j = 0; j < dimensions.length; j++)
                    if (dimensions[j].equals(b))
                        return cost[i][j];
        return 0;
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag compoundTag, HolderLookup.@NotNull Provider provider) {
        ListTag locationsTag = new ListTag();
        for (WarpLocation x : locations.values()) {
            CompoundTag newTag = new CompoundTag();
            newTag.putString("name", x.name);
            newTag.putIntArray("pos",new int[] {x.pos.getX(), x.pos.getY(), x.pos.getZ()});
            newTag.putBoolean("active", x.active);
            newTag.putInt("id", x.id);
            newTag.putString("level", x.level.toString());
            locationsTag.add(newTag);
        }

        compoundTag.put("locations", locationsTag);
        return compoundTag;
    }
    public static WarpLocations load(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        WarpLocations data = WarpLocations.create();
        for (Tag x : tag.getList("locations", ListTag.TAG_COMPOUND)) {
            if (x instanceof CompoundTag y) {
                int[] posArray = y.getIntArray("pos");
                BlockPos pos = BlockPos.containing(posArray[0], posArray[1], posArray[2]);
                WarpLocation location = new WarpLocation(y.getString("name"),pos,y.getInt("id"), y.getBoolean("active"), y.getString("level"));
                data.locations.put(location.id, location);
            }
        }
        for (WarpLocation x : data.locations.values())
            BlueMapIntegration.addMarker(x.pos, x.name, x.id);
        return data;
    }
    public HashMap<Integer, WarpLocation> getLocations() {
        return locations;
    }
    private int getLargestID(HashMap<Integer, WarpLocation> map) {
        int max = -1;
        for (WarpLocation x : map.values())
            if (x.id > max) max = x.id;
        return max;
    }
    public int addLocation(String name, BlockPos pos, boolean active, String level) {
        WarpLocation newLocation = new WarpLocation(name, pos, !locations.isEmpty() ? getLargestID(locations)+1 : 0, active, level);
        for (WarpLocation x : locations.values())
            if (x.equals(newLocation))
                return -1;
        locations.put(newLocation.id, newLocation);
        BlueMapIntegration.addMarker(pos, name, newLocation.id);
        this.setDirty();
        return newLocation.id;
    }
    public boolean setActive(int id, boolean active) {
        this.setDirty();
        locations.get(id).active = active;
        return true;
    }
    public boolean removeLocation(int id) {
        this.setDirty();
        BlueMapIntegration.removeMarker(id);
        return locations.remove(id)!=null;
    }
    public boolean setName(int id, String name) {
        this.setDirty();
        locations.get(id).name = name;
        return true;
    }
    public int getId(BlockPos pos) {
        for (WarpLocation x : locations.values())
            if (x.pos.equals(pos))
                return x.id;
        return -1;
    }
    public int search(String name) {
        for (WarpLocation x : locations.values())
            if (x.name.equals(name))
                return x.id;
        return -1;
    }
    public WarpLocation getLocation(int id) {
        for (WarpLocation x : locations.values())
            if (x.id == id)
                return x;
        return null;
    }
}
