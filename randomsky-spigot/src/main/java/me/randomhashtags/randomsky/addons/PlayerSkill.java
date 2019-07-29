package me.randomhashtags.randomsky.addons;

import me.randomhashtags.randomsky.addons.utils.Itemable;

import java.util.LinkedHashMap;

public interface PlayerSkill extends Itemable {
    LinkedHashMap<Integer, PlayerSkillLevel> getLevels();
}
