package me.randomhashtags.randomsky.addon.island;

import me.randomhashtags.randomsky.addon.util.Itemable;

import java.util.HashMap;
import java.util.List;

public interface IslandUpgrade extends Itemable {
    int getSlot();
    int getMaxTier();
    HashMap<Integer, List<String>> getCost();
    HashMap<Integer, List<String>> getUpgradeBonuses();
}
