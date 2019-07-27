package me.randomhashtags.randomsky.addons;

import me.randomhashtags.randomsky.addons.utils.Itemable;

import java.math.BigDecimal;
import java.util.HashMap;

public abstract class IslandProgressiveSkill extends Itemable {
    public abstract IslandProgressiveSkill getRequiredCompletedSkill();
    public abstract IslandLevel getRequiredLevel();
    public abstract boolean isCompleted(HashMap<String, BigDecimal> progress);
    public abstract boolean keepsCountingAfterCompletion();
}
