package me.randomhashtags.randomsky.addon.adventure;

import me.randomhashtags.randomsky.addon.util.Itemable;
import me.randomhashtags.randomsky.addon.util.Slotable;
import org.bukkit.Location;

import java.util.List;

public interface Adventure extends Itemable, Slotable { // TODO: add chest tiers
    String getName();
    Location getCenter();
    List<String> getBlacklistedItems();
    AdventureMap getRequiredMap();

    List<String> getChestLocations();
    float getTeleportDelay();
    List<String> getTeleportLocations();

    int getMaxPlayers();
}
