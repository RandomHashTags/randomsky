package me.randomhashtags.randomsky.addon.island;

import me.randomhashtags.randomsky.addon.util.Attributable;
import me.randomhashtags.randomsky.addon.util.Itemable;
import me.randomhashtags.randomsky.addon.util.Slotable;

import java.io.File;

public interface IslandOrigin extends Itemable, Slotable, Attributable {
    String getName();
    File getSchematic();
}
