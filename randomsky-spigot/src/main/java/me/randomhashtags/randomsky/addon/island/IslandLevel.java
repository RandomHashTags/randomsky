package me.randomhashtags.randomsky.addon.island;

import me.randomhashtags.randomsky.addon.util.Attributable;
import me.randomhashtags.randomsky.addon.util.Identifiable;
import me.randomhashtags.randomsky.addon.util.RequiredIslandLevel;
import me.randomhashtags.randomsky.addon.util.Slotable;

import java.util.List;

public interface IslandLevel extends Identifiable, RequiredIslandLevel, Attributable, Slotable {
    int getLevel();
    List<String> getCost();
}
