package me.randomhashtags.randomsky.addons;

import me.randomhashtags.randomsky.addons.utils.Itemable;

import java.util.LinkedHashMap;

public abstract class IslandSkill extends Itemable {
    public abstract String getString();
    public abstract LinkedHashMap<String, IslandProgressiveSkill> getSkills();
}
