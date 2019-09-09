package me.randomhashtags.randomsky.addons.island;

import me.randomhashtags.randomsky.addons.util.Itemable;

import java.util.HashMap;
import java.util.List;

public interface IslandUpgrade extends Itemable {
    int getSlot();
    int getMaxTier();
    HashMap<Integer, List<String>> getCost();
    HashMap<Integer, List<String>> getUpgradeBonuses();
}
