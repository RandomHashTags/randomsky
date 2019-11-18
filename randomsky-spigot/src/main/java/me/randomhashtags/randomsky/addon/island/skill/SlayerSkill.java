package me.randomhashtags.randomsky.addon.island.skill;

import me.randomhashtags.randomsky.addon.island.IslandSkill;
import org.bukkit.entity.EntityType;

public interface SlayerSkill extends IslandSkill {
    EntityType getEntity();
    SlayerSkill getRequiredCompletedSlayerSkill();
}
