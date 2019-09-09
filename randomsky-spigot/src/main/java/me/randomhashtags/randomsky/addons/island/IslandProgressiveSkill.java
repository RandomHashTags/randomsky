package me.randomhashtags.randomsky.addons.island;

import me.randomhashtags.randomsky.addons.util.Itemable;

import java.math.BigDecimal;
import java.util.HashMap;

public interface IslandProgressiveSkill extends Itemable {
    IslandProgressiveSkill getRequiredCompletedSkill();
    IslandLevel getRequiredLevel();
    boolean isCompleted(HashMap<String, BigDecimal> progress);
    boolean keepsCountingAfterCompletion();
}