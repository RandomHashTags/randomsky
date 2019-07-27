package me.randomhashtags.randomsky.addons;

import me.randomhashtags.randomsky.addons.utils.Itemable;

import java.util.HashMap;
import java.util.List;

public abstract class IslandUpgrade extends Itemable {
    public abstract int getSlot();
    public abstract int getMaxTier();
    public abstract HashMap<Integer, List<String>> getCost();
    public abstract HashMap<Integer, List<String>> getUpgradeBonuses();
}
