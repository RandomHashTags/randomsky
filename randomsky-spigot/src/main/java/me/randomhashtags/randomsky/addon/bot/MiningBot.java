package me.randomhashtags.randomsky.addon.bot;

public interface MiningBot extends AutoBot {
    int getRadius();
    long getScanInterval();
    int getBlocksPerInterval();
}
