package me.randomhashtags.randomsky.addons.autobots;

import me.randomhashtags.randomsky.addons.AutoBot;

public abstract class AutoMiningBot extends AutoBot {
    public abstract int getRadius();
    public abstract long getScanInterval();
    public abstract int getBlocksPerInterval();
}
