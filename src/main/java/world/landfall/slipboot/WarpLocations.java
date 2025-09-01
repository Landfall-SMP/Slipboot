package world.landfall.slipboot;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;
import net.neoforged.neoforge.network.PacketDistributor;
import world.landfall.slipboot.networking.UpdateWarpLocationPacket;

import java.util.HashMap;

public class WarpLocations extends SavedData {

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
        locations = new HashMap<>();
    }
    public static WarpLocations create() {
        return new WarpLocations();
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
            newTag.putString("level", x.level);
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
        if (Config.doBluemapIntegration) {
            try {
                for (WarpLocation x : data.locations.values())
                    BlueMapIntegration.addMarker(x.pos, x.name, x.id, ResourceLocation.parse(x.level));
            } catch (NoClassDefFoundError | Exception e) {
                Slipboot.LOGGER.warn("Failed to add BlueMap markers: " + e.getMessage());
            }
        }
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
        if (Config.doBluemapIntegration) {
            try {
                BlueMapIntegration.addMarker(pos, name, newLocation.id, ResourceLocation.parse(newLocation.level));
            } catch (NoClassDefFoundError | Exception e) {
                Slipboot.LOGGER.warn("Failed to add BlueMap marker: " + e.getMessage());
            }
        }
        this.setDirty();
        
        // Send update to all clients
        PacketDistributor.sendToAllPlayers(
            UpdateWarpLocationPacket.createAdd(newLocation.id, name, pos, active, level)
        );
        
        return newLocation.id;
    }
    public boolean setActive(int id, boolean active) {
        this.setDirty();
        WarpLocation loc = locations.get(id);
        if (loc != null) {
            loc.active = active;
            
            // Send update to all clients
            PacketDistributor.sendToAllPlayers(
                UpdateWarpLocationPacket.createUpdateActive(id, active)
            );
        }
        return true;
    }
    public boolean removeLocation(int id) {
        this.setDirty();
        if (Config.doBluemapIntegration) {
            try {
                BlueMapIntegration.removeMarker(id, ResourceLocation.parse(locations.get(id).level));
            } catch (NoClassDefFoundError | Exception e) {
                Slipboot.LOGGER.warn("Failed to remove BlueMap marker: " + e.getMessage());
            }
        }

        boolean removed = locations.remove(id) != null;
        if (removed) {
            // Send update to all clients
            PacketDistributor.sendToAllPlayers(
                UpdateWarpLocationPacket.createRemove(id)
            );
        }
        return removed;
    }
    public boolean setName(int id, String name) {
        this.setDirty();
        WarpLocation loc = locations.get(id);
        if (loc != null) {
            loc.name = name;
            
            // Send update to all clients
            PacketDistributor.sendToAllPlayers(
                UpdateWarpLocationPacket.createUpdateName(id, name)
            );
        }
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
