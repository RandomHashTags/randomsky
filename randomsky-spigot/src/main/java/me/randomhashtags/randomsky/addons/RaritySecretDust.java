package me.randomhashtags.randomsky.addons;

import me.randomhashtags.randomsky.addons.utils.Itemable;

import java.util.LinkedHashMap;

public interface RaritySecretDust extends Itemable {
    int getMin();
    int getMax();
    LinkedHashMap<CustomEnchantDust, Integer> getRevealChances();
}
