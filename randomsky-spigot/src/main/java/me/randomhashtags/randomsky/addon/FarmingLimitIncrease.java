package me.randomhashtags.randomsky.addon;

import me.randomhashtags.randomsky.addon.util.Percentable;

import java.util.List;

public interface FarmingLimitIncrease extends Percentable {
    int getMinPercent();
    int getMaxPercent();
    long getDuration();
    List<FarmingRecipe> getAppliesTo();
}
