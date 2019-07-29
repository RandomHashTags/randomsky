package me.randomhashtags.randomsky.addons;

import org.bukkit.util.Vector;

public interface AutoPlanter extends AutoBot {
    Vector getRePlantRadius();
    long getRePlantInterval();
    long getRePlantPerInterval();
}
