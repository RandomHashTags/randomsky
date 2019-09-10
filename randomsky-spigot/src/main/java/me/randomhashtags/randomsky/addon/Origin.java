package me.randomhashtags.randomsky.addon;

import me.randomhashtags.randomsky.addon.util.Itemable;

import java.io.File;
import java.util.List;

public interface Origin extends Itemable {
    String getString();
    int getSlot();
    File getSchematic();
    List<String> getPerks();
}
