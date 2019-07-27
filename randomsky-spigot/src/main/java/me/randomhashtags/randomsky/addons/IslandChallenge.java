package me.randomhashtags.randomsky.addons;

import java.math.BigDecimal;
import java.util.List;

public abstract class IslandChallenge {
    public abstract String getName();
    public abstract IslandChallenge getRequired();
    public abstract BigDecimal getCompletion();
    public abstract List<String> getObjective();
    public abstract List<String> getRewards();
}
