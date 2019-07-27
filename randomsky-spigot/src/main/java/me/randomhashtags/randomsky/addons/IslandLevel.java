package me.randomhashtags.randomsky.addons;

import java.math.BigDecimal;
import java.util.List;

public abstract class IslandLevel {
    public abstract int getLevel();
    public abstract IslandLevel getRequiredLevel();
    public abstract BigDecimal getCost();
    public abstract List<String> getLevelRewards();
}
