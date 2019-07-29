package me.randomhashtags.randomsky.addons;

import me.randomhashtags.randomsky.addons.utils.Itemable;

import java.util.HashMap;
import java.util.List;

public interface IslandUpgrade extends Itemable {
    int getSlot();
    int getMaxTier();
    HashMap<Integer, List<String>> getCost();
    HashMap<Integer, List<String>> getUpgradeBonuses();
}
