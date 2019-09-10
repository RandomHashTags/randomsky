package me.randomhashtags.randomsky.addon;

import me.randomhashtags.randomsky.addon.util.Itemable;

import java.util.List;

public interface FarmingLimitIncrease extends Itemable {
    int getMaxPercent();
    int getMinPercent();
    long getDuration();
    List<FarmingRecipe> getAppliesTo();
}
