package me.randomhashtags.randomsky.addons.bots;

import me.randomhashtags.randomsky.addons.AutoBot;

public interface SellBot extends AutoBot {
    long getSellInterval();
    double getSellValueMultiplier();
}
