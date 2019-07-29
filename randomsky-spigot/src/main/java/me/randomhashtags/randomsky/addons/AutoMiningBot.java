package me.randomhashtags.randomsky.addons;

public interface AutoMiningBot extends AutoBot {
    int getRadius();
    long getScanInterval();
    int getBlocksPerInterval();
}
