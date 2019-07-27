package me.randomhashtags.randomsky.addons.autobots;

import me.randomhashtags.randomsky.addons.AutoBot;

public abstract class AutoSellBot extends AutoBot {
    public abstract long getSellInterval();
    public abstract double getSellValueMultiplier();
}
