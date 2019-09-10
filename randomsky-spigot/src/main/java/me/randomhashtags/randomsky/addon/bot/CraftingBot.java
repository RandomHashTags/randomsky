package me.randomhashtags.randomsky.addon.bot;

public interface CraftingBot extends AutoBot {
    long getCraftScanSpeed();
    int getCraftedPerInterval();
    int getRecipePoolLimit();
    long getHopperTransferDelay();
    int getHopperTransferAmount();
}
