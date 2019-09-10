package me.randomhashtags.randomsky.addon;

import me.randomhashtags.randomsky.addon.util.Itemable;

import java.util.LinkedHashMap;

public interface PlayerSkill extends Itemable {
    LinkedHashMap<Integer, PlayerSkillLevel> getLevels();
}
