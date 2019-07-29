package me.randomhashtags.randomsky.addons;

import me.randomhashtags.randomsky.addons.utils.Itemable;

import java.util.List;

public interface FarmingLimitIncrease extends Itemable {
    int getMaxPercent();
    int getMinPercent();
    long getDuration();
    List<FarmingRecipe> getAppliesTo();
}
