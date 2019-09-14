package me.randomhashtags.randomsky.addon.bot;

import me.randomhashtags.randomsky.addon.util.Itemable;
import me.randomhashtags.randomsky.addon.util.MaxLevelable;

import java.math.BigDecimal;
import java.util.HashMap;

public interface AutoBotUpgrade extends Itemable, MaxLevelable {
    HashMap<Integer, BigDecimal> getValues();
    default BigDecimal getValue(int level) { return getValues().getOrDefault(level, BigDecimal.ZERO); }

    HashMap<Integer, BigDecimal> getCosts();
    default BigDecimal getCost(int level) { return getCosts().getOrDefault(level, BigDecimal.ZERO); }

    void use();
}
