package me.randomhashtags.randomsky.addon.adventure;

import me.randomhashtags.randomsky.addon.util.Itemable;
import me.randomhashtags.randomsky.addon.util.Nameable;
import me.randomhashtags.randomsky.addon.util.Slotable;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Adventure extends Itemable, Slotable, Nameable { // TODO: add chest tiers
    Location getCenter();
    List<String> getBlacklistedItems();
    AdventureMap getRequiredMap();

    List<String> getChestLocations();
    float getTeleportDelay();
    List<String> getTeleportLocations();

    int getMaxPlayers();
    List<Player> getPlayers();

    void join(@NotNull Player player);
}
