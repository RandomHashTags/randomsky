package me.randomhashtags.randomsky.addons.active;

import me.randomhashtags.randomsky.addons.island.IslandProgressiveSkill;

import java.math.BigDecimal;
import java.util.HashMap;

public class ActiveIslandProgressiveSkill {
    private IslandProgressiveSkill skill;
    private HashMap<String, BigDecimal> progress;
    public ActiveIslandProgressiveSkill(IslandProgressiveSkill skill, HashMap<String, BigDecimal> progress) {
        this.skill = skill;
        this.progress = progress;
    }
    public boolean isCompleted() { return skill.isCompleted(progress); }
    public IslandProgressiveSkill getSkill() { return skill; }
    public HashMap<String, BigDecimal> getProgress() { return progress; }
    public void setProgress(String key, BigDecimal value) { progress.put(key, value); }
}
