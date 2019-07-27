package me.randomhashtags.randomsky.addons;

import me.randomhashtags.randomsky.addons.utils.Itemable;

import java.util.List;

public abstract class CustomEnchantDust extends Itemable {
    public abstract int getMin();
    public abstract int getMax();
    public abstract List<CustomEnchantRarity> getAppliesToRarities();
}
