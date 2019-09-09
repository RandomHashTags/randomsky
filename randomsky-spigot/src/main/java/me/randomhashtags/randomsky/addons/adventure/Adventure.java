package me.randomhashtags.randomsky.addons.adventure;

import me.randomhashtags.randomsky.addons.util.Itemable;
import me.randomhashtags.randomsky.addons.util.Rewardable;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.List;

public interface Adventure extends Itemable, Rewardable {
    String getName();
    int getSlot();
    List<String> getItemLimitations();
    AdventureMap getRequiredMap();

    List<String> getChestLocations();
    float getTeleportDelay();
    List<String> getTeleportLocations();

    BigDecimal getMaxPlayers();
    List<Player> getPlayers();
}
