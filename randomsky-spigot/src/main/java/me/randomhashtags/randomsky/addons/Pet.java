package me.randomhashtags.randomsky.addons;

import me.randomhashtags.randomsky.addons.utils.Itemable;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

public interface Pet extends Itemable {
    long getCooldown();
    int getMaxLevel();
    LinkedHashMap<Integer, BigDecimal> getRequiredExpForLevel();
}
