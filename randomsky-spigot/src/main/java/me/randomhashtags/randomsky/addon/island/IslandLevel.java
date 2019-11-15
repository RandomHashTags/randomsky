package me.randomhashtags.randomsky.addon.island;

import me.randomhashtags.randomsky.addon.util.Identifiable;
import me.randomhashtags.randomsky.addon.util.RequiredIslandLevel;

import java.math.BigDecimal;
import java.util.List;

public interface IslandLevel extends Identifiable, RequiredIslandLevel {
    int getLevel();
    BigDecimal getCost();
    List<String> getLevelRewards();
}
