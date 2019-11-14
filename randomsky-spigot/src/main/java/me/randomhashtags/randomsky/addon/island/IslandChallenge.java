package me.randomhashtags.randomsky.addon.island;

import me.randomhashtags.randomsky.addon.util.Attributable;
import me.randomhashtags.randomsky.addon.util.Identifiable;
import me.randomhashtags.randomsky.addon.util.Slotable;

import java.math.BigDecimal;
import java.util.List;

public interface IslandChallenge extends Identifiable, Slotable, Attributable {
    String getName();
    IslandChallenge getRequired();
    BigDecimal getCompletion();
    List<String> getObjective();
    List<String> getRewards();
}
