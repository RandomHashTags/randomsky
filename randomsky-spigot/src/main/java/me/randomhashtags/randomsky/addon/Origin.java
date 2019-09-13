package me.randomhashtags.randomsky.addon;

import me.randomhashtags.randomsky.addon.util.Itemable;
import me.randomhashtags.randomsky.addon.util.Slotable;

import java.io.File;
import java.util.List;

public interface Origin extends Itemable, Slotable {
    String getString();
    File getSchematic();
    List<String> getPerks();
}
