package me.randomhashtags.randomsky.addon.bot;

public interface SellBot extends AutoBot {
    long getSellInterval();
    double getSellValueMultiplier();
}
