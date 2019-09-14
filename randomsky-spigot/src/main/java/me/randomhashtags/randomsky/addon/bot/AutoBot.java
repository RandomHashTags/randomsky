package me.randomhashtags.randomsky.addon.bot;

import me.randomhashtags.randomsky.addon.island.IslandUpgrade;
import me.randomhashtags.randomsky.addon.util.Attributable;
import me.randomhashtags.randomsky.addon.util.Itemable;
import me.randomhashtags.randomsky.addon.util.Nameable;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public interface AutoBot extends Itemable, Attributable, Nameable {
    String getType();
    int getInventorySize();
    ItemStack[] getInventory();
    HashMap<Integer, IslandUpgrade> getUpgrades();
}
