package me.randomhashtags.randomsky.addon.island;

import me.randomhashtags.randomsky.addon.util.Itemable;

import java.util.LinkedHashMap;

public interface IslandSkill extends Itemable {
    String getString();
    LinkedHashMap<String, IslandProgressiveSkill> getSkills();
}
