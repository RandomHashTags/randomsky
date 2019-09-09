package me.randomhashtags.randomsky.addons;

import me.randomhashtags.randomsky.addons.util.Itemable;

import java.util.LinkedHashMap;

public interface PlayerSkill extends Itemable {
    LinkedHashMap<Integer, PlayerSkillLevel> getLevels();
}
