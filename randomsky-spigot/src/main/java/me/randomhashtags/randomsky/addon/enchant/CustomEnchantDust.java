package me.randomhashtags.randomsky.addon.enchant;

import me.randomhashtags.randomsky.addon.util.Itemable;

import java.util.List;

public interface CustomEnchantDust extends Itemable {
    int getMin();
    int getMax();
    List<CustomEnchantRarity> getAppliesToRarities();
}
