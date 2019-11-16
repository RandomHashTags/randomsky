package me.randomhashtags.randomsky.addon;

import me.randomhashtags.randomsky.addon.util.Itemable;
import me.randomhashtags.randomsky.addon.util.RequiredPlayerRank;

public interface ColorCrystal extends Itemable, RequiredPlayerRank {
    String getEffect();
}
