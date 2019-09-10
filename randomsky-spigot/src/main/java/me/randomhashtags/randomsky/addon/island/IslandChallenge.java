package me.randomhashtags.randomsky.addon.island;

import java.math.BigDecimal;
import java.util.List;

public interface IslandChallenge {
    String getName();
    IslandChallenge getRequired();
    BigDecimal getCompletion();
    List<String> getObjective();
    List<String> getRewards();
}