package me.randomhashtags.randomsky.addon.island.skill;

import me.randomhashtags.randomsky.addon.ResourceNode;
import me.randomhashtags.randomsky.addon.island.IslandSkill;

public interface MiningSkill extends IslandSkill {
    ResourceNode getTrackedNode();
}
