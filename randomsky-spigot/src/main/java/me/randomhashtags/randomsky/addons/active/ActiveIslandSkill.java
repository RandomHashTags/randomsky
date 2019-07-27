package me.randomhashtags.randomsky.addons.active;

import me.randomhashtags.randomsky.addons.IslandSkill;

import java.util.LinkedHashMap;

public class ActiveIslandSkill {
    private IslandSkill skill;
    private int level;
    private LinkedHashMap<String, ActiveIslandProgressiveSkill> skills;
    public ActiveIslandSkill(IslandSkill skill, int level, LinkedHashMap<String, ActiveIslandProgressiveSkill> skills) {
        this.skill = skill;
        this.level = level;
        this.skills = skills;
    }
    public IslandSkill getSkill() { return skill; }
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    public LinkedHashMap<String, ActiveIslandProgressiveSkill> getSkills() { return skills; }
}
