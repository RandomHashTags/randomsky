package me.randomhashtags.randomsky.addons;

import me.randomhashtags.randomsky.utils.objects.PolyBoundary;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;

public interface IslandV2 {
    void load();
    void unload();

    UUID getUUID();
    IslandLevel getLevel();
    Origin getOrigin();
    PolyBoundary getRadius();
    List<String> getTags();
    List<String> getAttributes();
    List<FarmingRecipe> getAllowedCrops();
    HashMap<UUID, IslandRank> getMembers();

    TreeMap<String, Location> getLocations();
    default boolean setLocation(String identifier, Location l) {
        final TreeMap<String, Location> a = getLocations();
        boolean b = a != null, exists = b && a.containsKey(identifier);
        if(b) a.put(identifier, l);
        return exists;
    }
    default Location getLocation(String identifier) {
        final TreeMap<String, Location> l = getLocations();
        return l != null ? l.get(identifier) : null;
    }
    default Location getCenter() { return getLocation("center"); }
    default Location getHome() { return getLocation("home"); }
    default Location getWarp() { return getLocation("warp"); }

    List<UUID> getBannedPlayers();

    HashMap<UUID, Double> getRatings();
}
