package me.randomhashtags.randomsky.addon.adventure;

import me.randomhashtags.randomsky.addon.util.Itemable;
import me.randomhashtags.randomsky.addon.util.Rewardable;
import me.randomhashtags.randomsky.addon.util.Slotable;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.List;

public interface Adventure extends Itemable, Rewardable, Slotable {
    String getName();
    List<String> getItemLimitations();
    AdventureMap getRequiredMap();

    List<String> getChestLocations();
    float getTeleportDelay();
    List<String> getTeleportLocations();

    BigDecimal getMaxPlayers();
    List<Player> getPlayers();
}
