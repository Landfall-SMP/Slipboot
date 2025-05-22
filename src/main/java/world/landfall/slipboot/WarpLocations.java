package world.landfall.slipboot;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

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
            return name.equals(e.name) && pos.equals(e.pos) && (active == e.active);
        }
    }
    private final Set<WarpLocation> locations;
    public WarpLocations() {
        locations = new HashSet<WarpLocation>();
    }
    public static WarpLocations create() {
        return new WarpLocations();
    }
    @Override
    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        return compoundTag;
    }
    public static WarpLocations load(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        WarpLocations data = WarpLocations.create();
        return data;
    }
    public Set<WarpLocation> getLocations() {
        return locations;
    }
    public boolean addLocation(String name, BlockPos pos, boolean active) {
        WarpLocation newLocation = new WarpLocation(name, pos, locations.size(), active);
        for (WarpLocation x : locations)
            if (x.equals(newLocation))
                return false;
        locations.add(newLocation);
        this.setDirty();
        return true;
    }
}
