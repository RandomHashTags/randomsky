package me.randomhashtags.randomsky.addons;

import me.randomhashtags.randomsky.addons.util.Itemable;

import java.util.List;

public interface CustomEnchantDust extends Itemable {
    int getMin();
    int getMax();
    List<CustomEnchantRarity> getAppliesToRarities();
}
