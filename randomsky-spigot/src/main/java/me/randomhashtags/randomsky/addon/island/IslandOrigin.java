package me.randomhashtags.randomsky.addon.island;

import me.randomhashtags.randomsky.addon.util.Itemable;
import me.randomhashtags.randomsky.addon.util.Slotable;

import java.io.File;
import java.util.List;

public interface IslandOrigin extends Itemable, Slotable {
    String getString();
    File getSchematic();
    List<String> getPerks();
}
