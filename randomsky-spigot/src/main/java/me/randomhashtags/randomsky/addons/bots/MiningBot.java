package me.randomhashtags.randomsky.addons.bots;

import me.randomhashtags.randomsky.addons.AutoBot;

public interface MiningBot extends AutoBot {
    int getRadius();
    long getScanInterval();
    int getBlocksPerInterval();
}
