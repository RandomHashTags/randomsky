package me.randomhashtags.randomsky.addon.island;

import me.randomhashtags.randomsky.addon.util.Attributable;
import me.randomhashtags.randomsky.addon.util.Identifiable;
import me.randomhashtags.randomsky.addon.util.Nameable;
import me.randomhashtags.randomsky.addon.util.Slotable;

import java.math.BigDecimal;
import java.util.List;

public interface IslandChallenge extends Identifiable, Slotable, Attributable, Nameable {
    IslandChallenge getRequired();
    BigDecimal getCompletion();
    List<String> getObjective();
    List<String> getRewards();
}
