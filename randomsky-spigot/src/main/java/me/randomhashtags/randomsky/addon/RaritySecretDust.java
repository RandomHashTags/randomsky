package me.randomhashtags.randomsky.addon;

import me.randomhashtags.randomsky.addon.enchant.CustomEnchantDust;
import me.randomhashtags.randomsky.addon.util.Itemable;

import java.util.LinkedHashMap;

public interface RaritySecretDust extends Itemable {
    int getMin();
    int getMax();
    LinkedHashMap<CustomEnchantDust, Integer> getRevealChances();
}
