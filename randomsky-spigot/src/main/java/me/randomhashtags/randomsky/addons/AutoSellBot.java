package me.randomhashtags.randomsky.addons;

public interface AutoSellBot extends AutoBot {
    long getSellInterval();
    double getSellValueMultiplier();
}
