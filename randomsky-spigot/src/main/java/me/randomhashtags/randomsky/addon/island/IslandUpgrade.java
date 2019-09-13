package me.randomhashtags.randomsky.addon.island;

import me.randomhashtags.randomsky.addon.util.Itemable;
import me.randomhashtags.randomsky.addon.util.MaxLevelable;
import me.randomhashtags.randomsky.addon.util.Slotable;

import java.util.HashMap;
import java.util.List;

public interface IslandUpgrade extends Itemable, MaxLevelable, Slotable {
    HashMap<Integer, List<String>> getCost();
    HashMap<Integer, List<String>> getUpgradeBonuses();
}
