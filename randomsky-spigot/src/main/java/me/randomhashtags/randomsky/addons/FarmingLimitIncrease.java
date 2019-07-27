package me.randomhashtags.randomsky.addons;

import me.randomhashtags.randomsky.addons.utils.Itemable;

import java.util.List;

public abstract class FarmingLimitIncrease extends Itemable {
    public abstract int getMaxPercent();
    public abstract int getMinPercent();
    public abstract long getDuration();
    public abstract List<FarmingRecipe> getAppliesTo();
}
