package me.randomhashtags.randomsky.addon.island.skill;

import me.randomhashtags.randomsky.addon.FarmingRecipe;
import me.randomhashtags.randomsky.addon.island.IslandSkill;

import java.math.BigDecimal;

public interface FarmingSkill extends IslandSkill {
    String getRequiredCompletedSkill();
    BigDecimal getDailyLimit();
    BigDecimal getCompletion();
    FarmingRecipe getRequiredRecipe();
}
