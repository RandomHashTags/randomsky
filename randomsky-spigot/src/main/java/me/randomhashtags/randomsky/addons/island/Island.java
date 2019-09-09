package me.randomhashtags.randomsky.addons.island;

import me.randomhashtags.randomsky.addons.FarmingRecipe;
import me.randomhashtags.randomsky.addons.Origin;
import me.randomhashtags.randomsky.addons.ResourceNode;
import me.randomhashtags.randomsky.addons.active.ActiveIslandChallenge;
import me.randomhashtags.randomsky.addons.active.ActiveIslandSkill;
import me.randomhashtags.randomsky.addons.active.ActivePermissionBlock;
import me.randomhashtags.randomsky.addons.active.ActiveResourceNode;
import me.randomhashtags.randomsky.addons.util.Attributable;
import me.randomhashtags.randomsky.addons.util.Loadable;
import me.randomhashtags.randomsky.utils.objects.PolyBoundary;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.math.BigDecimal;
import java.util.*;

public interface Island extends Attributable, Loadable {
    boolean isLoaded();
    UUID getUUID();
    long getCreatedTime();
    UUID getCreator();
    boolean isOpenToPublic();
    IslandLevel getLevel();
    Origin getOrigin();
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
    List<PotionEffectType> getImmuneTo();

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
