package me.randomhashtags.randomsky.addon.island;

import me.randomhashtags.randomsky.addon.FarmingRecipe;
import me.randomhashtags.randomsky.addon.ResourceNode;
import me.randomhashtags.randomsky.addon.active.ActiveIslandChallenge;
import me.randomhashtags.randomsky.addon.active.ActiveIslandSkill;
import me.randomhashtags.randomsky.addon.active.ActivePermissionBlock;
import me.randomhashtags.randomsky.addon.active.ActiveResourceNode;
import me.randomhashtags.randomsky.addon.obj.RSInvite;
import me.randomhashtags.randomsky.addon.util.Attributable;
import me.randomhashtags.randomsky.addon.util.Identifiable;
import me.randomhashtags.randomsky.addon.util.Loadable;
import me.randomhashtags.randomsky.util.obj.PolyBoundary;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.*;

public interface Island extends Identifiable, Attributable, Loadable {
    HashMap<UUID, Island> CACHE = new HashMap<>();
    static Island fromUUID(UUID uuid) {
        return null;
    }

    void delete();
    boolean isLoaded();
    UUID getUUID();
    long getCreatedTime();
    UUID getCreator();
    boolean isOpenToPublic();
    void setOpenToPublic(boolean isOpenToPublic);
    IslandLevel getIslandLevel();
    void setIslandLevel(IslandLevel level);
    IslandOrigin getOrigin();
    PolyBoundary getCurrentBoundary();
    PolyBoundary getMaxBoundary();
    String getTag();
    String getDescription();
    List<String> getTags();

    List<RSInvite> getInvites();
    void join(@NotNull Player player);

    void ban(@NotNull UUID player);

    List<FarmingRecipe> getAllowedCrops();
    List<ResourceNode> getAllowedNodes();
    List<String> getAllowedMobs();

    HashMap<UUID, Double> getRatings();
    HashMap<UUID, IslandRank> getMembers();

    default List<Player> getOnlineMembers() {
        final List<Player> p = new ArrayList<>();
        for(UUID u : getMembers().keySet()) {
            final OfflinePlayer o = Bukkit.getOfflinePlayer(u);
            if(o.isOnline()) {
                p.add(o.getPlayer());
            }
        }
        return p;
    }

    TreeMap<String, Location> getLocations();
    default void setLocation(@NotNull String identifier, @Nullable Location l) {
        final TreeMap<String, Location> a = getLocations();
        if(l == null) {
            a.remove(identifier);
        } else if(a != null) {
            a.put(identifier, l);
        }
    }
    default Location getLocation(@NotNull String identifier) {
        final TreeMap<String, Location> l = getLocations();
        return l != null ? l.get(identifier) : null;
    }
    default Location getCenter() { return getLocation("center"); }
    default Location getHome() { return getLocation("home"); }
    default void setHome(Location l) { setLocation("home", l); }
    default Location getWarp() { return getLocation("warp"); }
    default void setWarp(Location l) { setLocation("warp", l); }

    List<UUID> getBannedPlayers();

    HashMap<ResourceNode, BigDecimal> getMinedResourceNodes();
    HashMap<ResourceNode, Double> getResourceNodeRespawnRates();
    HashMap<FarmingRecipe, BigDecimal> getCropsGrown();
    HashMap<String, BigDecimal> getSlainMobs();
    HashMap<String, Double> getMobRespawnRates();

    List<ActiveIslandSkill> getSkills();
    List<String> getCompletedSkills();
    List<ActiveIslandChallenge> getChallenges();
    HashMap<String, Boolean> getCompletedChallenges();

    List<ActivePermissionBlock> getPermissionBlocks();
    List<ActiveResourceNode> getActiveResourceNodes();
}
