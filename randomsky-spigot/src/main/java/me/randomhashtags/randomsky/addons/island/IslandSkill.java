package me.randomhashtags.randomsky.addons.island;

import me.randomhashtags.randomsky.addons.util.Itemable;

import java.util.LinkedHashMap;

public interface IslandSkill extends Itemable {
    String getString();
    LinkedHashMap<String, IslandProgressiveSkill> getSkills();
}
