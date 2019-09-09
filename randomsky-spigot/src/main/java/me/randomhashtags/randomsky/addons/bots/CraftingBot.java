package me.randomhashtags.randomsky.addons.bots;

import me.randomhashtags.randomsky.addons.AutoBot;

public interface CraftingBot extends AutoBot {
    long getCraftScanSpeed();
    int getCraftedPerInterval();
    int getRecipePoolLimit();
    long getHopperTransferDelay();
    int getHopperTransferAmount();
}
