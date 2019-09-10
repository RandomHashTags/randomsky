package me.randomhashtags.randomsky.addon.island;

import me.randomhashtags.randomsky.addon.util.Itemable;

import java.math.BigDecimal;
import java.util.HashMap;

public interface IslandProgressiveSkill extends Itemable {
    IslandProgressiveSkill getRequiredCompletedSkill();
    IslandLevel getRequiredLevel();
    boolean isCompleted(HashMap<String, BigDecimal> progress);
    boolean keepsCountingAfterCompletion();
}
