package me.randomhashtags.randomsky.addon.bot;

import me.randomhashtags.randomsky.addon.util.Attributable;
import me.randomhashtags.randomsky.addon.util.Itemable;
import me.randomhashtags.randomsky.addon.util.Nameable;

import java.util.HashMap;

public interface AutoBot extends Itemable, Attributable, Nameable {
    String getType();
    HashMap<Integer, AutoBotUpgrade> getUpgrades();
}
