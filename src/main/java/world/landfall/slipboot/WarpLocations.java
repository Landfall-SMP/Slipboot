package world.landfall.slipboot;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Position;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WarpLocations extends SavedData {
    public static class WarpLocation {
        String name;
        int id;
        BlockPos pos;
        boolean active;
        public WarpLocation(String name, BlockPos pos, int id, boolean active) {
            this.name = name;
            this.pos = pos;
            this.active = active;
            this.id = id;
        }
        public boolean equals(WarpLocation e) {
            return pos.equals(e.pos);
        }
    }
    private final List<WarpLocation> locations;
    public WarpLocations() {
        locations = new ArrayList<WarpLocation>();
    }
    public static WarpLocations create() {
        return new WarpLocations();
    }
    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag compoundTag, HolderLookup.@NotNull Provider provider) {
        ListTag locationsTag = new ListTag();
        for (WarpLocation x : locations) {
            CompoundTag newTag = new CompoundTag();
            newTag.putString("name", x.name);
            newTag.putIntArray("pos",new int[] {x.pos.getX(), x.pos.getY(), x.pos.getZ()});
            newTag.putBoolean("active", x.active);
            newTag.putInt("id", x.id);
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
                WarpLocation location = new WarpLocation(y.getString("name"),pos,y.getInt("id"), y.getBoolean("active"));
                data.locations.add(location);
            }
        }
        return data;
    }
    public List<WarpLocation> getLocations() {
        return locations;
    }
    public int addLocation(String name, BlockPos pos, boolean active) {
        WarpLocation newLocation = new WarpLocation(name, pos, !locations.isEmpty() ? locations.getLast().id+1 : 0, active);
        for (WarpLocation x : locations)
            if (x.equals(newLocation))
                return -1;
        locations.add(newLocation);
        this.setDirty();
        return newLocation.id;
    }
    public boolean setActive(int id, boolean active) {
        for (int i = 0; i < locations.size(); i++) {
            if (locations.get(i).id == id) {
                locations.get(i).active = active;
                this.setDirty();
                return true;
            }
        }
        return false;
    }
    public boolean removeLocation(int id) {
        for (int i = 0; i < locations.size(); i++)
            if (locations.get(i).id == id) {
                locations.remove(i);
                this.setDirty();
                return true;
            }
        return false;
    }
    public boolean setName(int id, String name) {
        for (int i = 0; i < locations.size(); i++)
            if (locations.get(i).id == id) {
                locations.get(i).name = name;
                System.out.println("Set name of " + id + " to " + name);
                this.setDirty();
                return true;
            }
        return false;
    }
    public int getId(BlockPos pos) {
        for (WarpLocation x : locations)
            if (x.pos.equals(pos))
                return x.id;
        return -1;
    }
}
