package me.randomhashtags.randomsky.addon.island;

import me.randomhashtags.randomsky.addon.util.Itemable;
import me.randomhashtags.randomsky.addon.util.RequiredIslandLevel;

import java.math.BigDecimal;
import java.util.HashMap;

public interface IslandProgressiveSkill extends Itemable, RequiredIslandLevel {
    IslandProgressiveSkill getRequiredCompletedSkill();
    boolean isCompleted(HashMap<String, BigDecimal> progress);
    boolean keepsCountingAfterCompletion();
}
