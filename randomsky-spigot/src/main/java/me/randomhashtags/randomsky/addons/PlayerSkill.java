package me.randomhashtags.randomsky.addons;

import me.randomhashtags.randomsky.addons.utils.Itemable;

import java.util.LinkedHashMap;

public abstract class PlayerSkill extends Itemable {
    public abstract LinkedHashMap<Integer, PlayerSkillLevel> getLevels();
}
