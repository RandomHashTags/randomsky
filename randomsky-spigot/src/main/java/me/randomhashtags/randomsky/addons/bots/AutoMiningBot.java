package me.randomhashtags.randomsky.addons.bots;

import me.randomhashtags.randomsky.addons.AutoBot;

public interface AutoMiningBot extends AutoBot {
    int getRadius();
    long getScanInterval();
    int getBlocksPerInterval();
}
