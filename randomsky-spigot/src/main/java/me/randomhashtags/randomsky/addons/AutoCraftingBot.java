package me.randomhashtags.randomsky.addons;

public interface AutoCraftingBot extends AutoBot {
    long getCraftScanSpeed();
    int getCraftedPerInterval();
    int getRecipePoolLimit();
    long getHopperTransferDelay();
    int getHopperTransferAmount();
}
