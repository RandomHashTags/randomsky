package me.randomhashtags.randomsky.addon.bot;

import org.bukkit.util.Vector;

public interface PlanterBot extends AutoBot {
    Vector getRePlantRadius();
    long getRePlantInterval();
    long getRePlantPerInterval();
}
