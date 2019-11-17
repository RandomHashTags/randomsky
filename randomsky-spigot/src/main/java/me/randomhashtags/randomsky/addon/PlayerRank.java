package me.randomhashtags.randomsky.addon;

import me.randomhashtags.randomsky.addon.util.Attributable;
import me.randomhashtags.randomsky.addon.util.Itemable;

public interface PlayerRank extends Attributable, Itemable {
    int getRankValue();
    String getAppearance();
}
