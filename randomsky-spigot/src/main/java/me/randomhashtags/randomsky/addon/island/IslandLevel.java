package me.randomhashtags.randomsky.addon.island;

import java.math.BigDecimal;
import java.util.List;

public interface IslandLevel {
    int getLevel();
    IslandLevel getRequiredLevel();
    BigDecimal getCost();
    List<String> getLevelRewards();
}