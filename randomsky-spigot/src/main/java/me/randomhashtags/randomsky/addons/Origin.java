package me.randomhashtags.randomsky.addons;

import me.randomhashtags.randomsky.addons.utils.Itemable;

import java.io.File;
import java.util.List;

public abstract class Origin extends Itemable {
    public abstract String getString();
    public abstract int getSlot();
    public abstract File getSchematic();
    public abstract List<String> getPerks();
}
