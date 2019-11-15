package me.randomhashtags.randomsky.addon.island;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import me.randomhashtags.randomsky.addon.FarmingRecipe;
import me.randomhashtags.randomsky.addon.ResourceNode;
import me.randomhashtags.randomsky.addon.active.ActiveIslandChallenge;
import me.randomhashtags.randomsky.addon.active.ActiveIslandSkill;
import me.randomhashtags.randomsky.addon.active.ActivePermissionBlock;
import me.randomhashtags.randomsky.addon.active.ActiveResourceNode;
import me.randomhashtags.randomsky.addon.util.Attributable;
import me.randomhashtags.randomsky.addon.util.Identifiable;
import me.randomhashtags.randomsky.addon.util.Loadable;
import me.randomhashtags.randomsky.util.obj.PolyBoundary;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.*;

public interface Island extends Identifiable, Attributable, Loadable {
    boolean isLoaded();
    UUID getUUID();
    long getCreatedTime();
    UUID getCreator();
    boolean isOpenToPublic();
    IslandLevel getIslandLevel();
    IslandOrigin getOrigin();
    PolyBoundary getCurrentBoundary();
    PolyBoundary getMaxBoundary();
    String getTag();
    String getDescription();
    List<String> getTags();

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
    default Location getWarp() { return getLocation("warp"); }

    List<UUID> getBannedPlayers();

    HashMap<ResourceNode, BigDecimal> getMinedResourceNodes();
    HashMap<ResourceNode, Double> getResourceNodeRespawnRates();
    HashMap<FarmingRecipe, BigDecimal> getCropsGrown();
    HashMap<String, BigDecimal> getSlainMobs();
    HashMap<String, Double> getMobRespawnRates();

    List<ActiveIslandSkill> getSkills();
    List<String> getCompletedSkills();
    List<ActiveIslandChallenge> getChallenges();
    List<String> getCompletedChallenges();

    List<ActivePermissionBlock> getPermissionBlocks();
    List<ActiveResourceNode> getActiveResourceNodes();
}
