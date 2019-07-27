package me.randomhashtags.randomsky.addons;

import me.randomhashtags.randomsky.addons.utils.Itemable;

import java.util.LinkedHashMap;

public abstract class RaritySecretDust extends Itemable {
    public abstract int getMin();
    public abstract int getMax();
    public abstract LinkedHashMap<CustomEnchantDust, Integer> getRevealChances();
}
