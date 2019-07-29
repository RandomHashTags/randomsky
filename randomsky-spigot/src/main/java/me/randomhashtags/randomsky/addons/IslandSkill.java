package me.randomhashtags.randomsky.addons;

import me.randomhashtags.randomsky.addons.utils.Itemable;

import java.util.LinkedHashMap;

public interface IslandSkill extends Itemable {
    String getString();
    LinkedHashMap<String, IslandProgressiveSkill> getSkills();
}
