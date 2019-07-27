package me.randomhashtags.randomsky.addons.autobots;

import me.randomhashtags.randomsky.addons.AutoBot;
import org.bukkit.util.Vector;

public abstract class AutoPlanter extends AutoBot {
    public abstract Vector getRePlantRadius();
    public abstract long getRePlantInterval();
    public abstract long getRePlantPerInterval();
}
