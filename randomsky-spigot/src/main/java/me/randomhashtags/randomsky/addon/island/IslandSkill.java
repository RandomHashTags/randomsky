package me.randomhashtags.randomsky.addon.island;

import me.randomhashtags.randomsky.addon.util.Itemable;
import me.randomhashtags.randomsky.addon.util.Slotable;

import java.math.BigDecimal;

public interface IslandSkill extends Itemable, Slotable {
    String getType();
    BigDecimal getCompletion();
}
