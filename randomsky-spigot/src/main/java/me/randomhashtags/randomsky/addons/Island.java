package me.randomhashtags.randomsky.addons;

import me.randomhashtags.randomsky.addons.active.ActiveIslandChallenge;
import me.randomhashtags.randomsky.addons.active.ActiveIslandSkill;
import me.randomhashtags.randomsky.addons.active.ActiveIslandUpgrade;
import me.randomhashtags.randomsky.utils.RSStorage;
import me.randomhashtags.randomsky.utils.objects.PolyBoundary;
import org.bukkit.Location;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

public abstract class Island extends RSStorage {
    private boolean isLoaded = false;

    public void load() {
        if(!isLoaded) {
            isLoaded = true;
            didLoad();
        }
    }
    public void unload() {
        if(isLoaded) {
            isLoaded = false;
            didUnload();
        }
    }
    public abstract void didLoad();
    public abstract void didUnload();

    public boolean isLoaded() { return isLoaded; }
    public abstract UUID getUUID();
    public abstract Origin getOrigin();
    public abstract PolyBoundary getRadius();
    public abstract List<String> getTags();
    public abstract List<ActiveIslandUpgrade> getUpgrades();
    public abstract List<ActiveIslandChallenge> getChallenges();
    public abstract List<ActiveIslandSkill> getActiveSkills();

    public abstract HashMap<UUID, IslandRank> getMembers();
    public abstract List<UUID> getBanned();

    public abstract LinkedHashMap<UUID, Integer> getRatings();

    public abstract List<PotionEffectType> getImmuneTo();

    public abstract List<UUID> getBannedPlayers();

    public abstract List<FarmingRecipe> getAllowedCrops();

    public abstract HashMap<String, Location> getLocations();
    public void setLocation(String key, Location location) {
        final HashMap<String, Location> l = getLocations();
        if(l != null) l.put(key, location);
    }
    private Location getLocation(String key) {
        final HashMap<String, Location> l = getLocations();
        return l != null ? l.get(key) : null;
    }
    public Location getCenter() { return getLocation("center"); }
    public Location getHome() { return getLocation("home"); }
    public Location getWarp() { return getLocation("warp"); }
}
