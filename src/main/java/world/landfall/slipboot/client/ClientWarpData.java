package world.landfall.slipboot.client;

import net.minecraft.core.BlockPos;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import world.landfall.slipboot.Slipboot;
import world.landfall.slipboot.Constants;
import world.landfall.slipboot.WarpLocations;
import world.landfall.slipboot.networking.SyncWarpLocationsPacket;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Client-side storage for warp location data.
 * This is populated via packets from the server.
 */
@OnlyIn(Dist.CLIENT)
public class ClientWarpData {
    private static final ConcurrentHashMap<Integer, WarpLocations.WarpLocation> locations = new ConcurrentHashMap<>();
    private static final int MAX_LOCATIONS = 1000; // Prevent memory issues
    
    /**
     * Get all warp locations on the client
     * Returns a thread-safe copy to prevent concurrent modification
     */
    public static Map<Integer, WarpLocations.WarpLocation> getLocations() {
        return new ConcurrentHashMap<>(locations);
    }
    
    /**
     * Get a specific warp location by ID
     */
    public static WarpLocations.WarpLocation getLocation(int id) {
        return locations.get(id);
    }
    
    /**
     * Clear all client-side warp data
     */
    public static void clear() {
        locations.clear();
        Slipboot.LOGGER.debug("Cleared client warp data");
    }
    
    /**
     * Update all locations from a sync packet
     */
    public static void updateAllLocations(List<SyncWarpLocationsPacket.WarpLocationData> newLocations) {
        // Validate size limit
        if (newLocations.size() > MAX_LOCATIONS) {
            Slipboot.LOGGER.warn("Received {} locations, exceeding max of {}. Truncating.", newLocations.size(), MAX_LOCATIONS);
            newLocations = newLocations.subList(0, MAX_LOCATIONS);
        }
        
        clear();
        for (SyncWarpLocationsPacket.WarpLocationData data : newLocations) {
            // Validate data before adding
            if (data.id() >= 0 && data.name() != null && data.pos() != null && data.level() != null) {
                locations.put(data.id(), new WarpLocations.WarpLocation(
                    Constants.limitLength(data.name(), Constants.MAX_PACKET_NAME_LENGTH),
                    data.pos(),
                    data.id(),
                    data.active(),
                    data.level()
                ));
            }
        }
        Slipboot.LOGGER.info("Updated client warp data with {} locations", locations.size());
    }
    
    /**
     * Add or update a single location
     */
    public static void addLocation(int id, String name, BlockPos pos, boolean active, String level) {
        // Validate inputs
        if (id < 0 || name == null || pos == null || level == null) {
            Slipboot.LOGGER.warn("Invalid warp location data received: id={}, name={}, pos={}, level={}", id, name, pos, level);
            return;
        }
        
        // Check size limit
        if (locations.size() >= MAX_LOCATIONS && !locations.containsKey(id)) {
            Slipboot.LOGGER.warn("Cannot add warp location {}: max locations ({}) reached", id, MAX_LOCATIONS);
            return;
        }
        
        String safeName = Constants.limitLength(name, Constants.MAX_PACKET_NAME_LENGTH);
        locations.put(id, new WarpLocations.WarpLocation(safeName, pos, id, active, level));
        Slipboot.LOGGER.debug("Added/updated warp location {} on client", id);
    }
    
    /**
     * Remove a location
     */
    public static void removeLocation(int id) {
        if (locations.remove(id) != null) {
            Slipboot.LOGGER.debug("Removed warp location {} on client", id);
        }
    }
    
    /**
     * Update the name of a location
     */
    public static void updateName(int id, String name) {
        if (name == null) {
            Slipboot.LOGGER.warn("Null name received for warp location {}", id);
            return;
        }
        
        locations.compute(id, (key, loc) -> {
            if (loc != null) {
                // Create new immutable location with updated name
                String safeName = Constants.limitLength(name, Constants.MAX_PACKET_NAME_LENGTH);
                WarpLocations.WarpLocation newLoc = new WarpLocations.WarpLocation(
                    safeName, loc.pos, loc.id, loc.active, loc.level
                );
                Slipboot.LOGGER.debug("Updated name for warp location {} on client", id);
                return newLoc;
            }
            return null;
        });
    }
    
    /**
     * Update the active state of a location
     */
    public static void updateActive(int id, boolean active) {
        locations.compute(id, (key, loc) -> {
            if (loc != null) {
                // Create new immutable location with updated active state
                WarpLocations.WarpLocation newLoc = new WarpLocations.WarpLocation(
                    loc.name, loc.pos, loc.id, active, loc.level
                );
                Slipboot.LOGGER.debug("Updated active state for warp location {} on client", id);
                return newLoc;
            }
            return null;
        });
    }
}