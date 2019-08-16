package me.randomhashtags.randomsky.addons.bots;

import me.randomhashtags.randomsky.addons.AutoBot;
import org.bukkit.util.Vector;

public interface AutoPlanter extends AutoBot {
    Vector getRePlantRadius();
    long getRePlantInterval();
    long getRePlantPerInterval();
}
