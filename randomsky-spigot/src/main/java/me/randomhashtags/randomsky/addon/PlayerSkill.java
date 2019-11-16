package me.randomhashtags.randomsky.addon;

import me.randomhashtags.randomsky.addon.util.Itemable;
import me.randomhashtags.randomsky.addon.util.MaxLevelable;
import me.randomhashtags.randomsky.util.universal.UInventory;

import java.util.HashMap;

public interface PlayerSkill extends Itemable, MaxLevelable {
    String getName();
    UInventory getUInventory();
    HashMap<Integer, PlayerSkillLevel> getLevels();
}
