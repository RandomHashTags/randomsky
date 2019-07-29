package me.randomhashtags.randomsky.addons;

import me.randomhashtags.randomsky.addons.utils.Itemable;

import java.util.List;

public interface CustomEnchantDust extends Itemable {
    int getMin();
    int getMax();
    List<CustomEnchantRarity> getAppliesToRarities();
}
