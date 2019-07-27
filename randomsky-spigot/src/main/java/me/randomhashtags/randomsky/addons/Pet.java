package me.randomhashtags.randomsky.addons;

import me.randomhashtags.randomsky.addons.utils.Itemable;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

public abstract class Pet extends Itemable {
    public abstract long getCooldown();
    public abstract int getMaxLevel();
    public abstract LinkedHashMap<Integer, BigDecimal> getRequiredExpForLevel();
}
