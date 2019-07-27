package me.randomhashtags.randomsky.addons.autobots;

import me.randomhashtags.randomsky.addons.AutoBot;

public abstract class AutoCraftingBot extends AutoBot {
    public abstract long getCraftScanSpeed();
    public abstract int getCraftedPerInterval();
    public abstract int getRecipePoolLimit();
    public abstract long getHopperTransferDelay();
    public abstract int getHopperTransferAmount();
}
