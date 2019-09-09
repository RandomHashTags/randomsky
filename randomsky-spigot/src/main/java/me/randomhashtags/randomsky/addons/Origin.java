package me.randomhashtags.randomsky.addons;

import me.randomhashtags.randomsky.addons.util.Itemable;

import java.io.File;
import java.util.List;

public interface Origin extends Itemable {
    String getString();
    int getSlot();
    File getSchematic();
    List<String> getPerks();
}
