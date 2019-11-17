package me.randomhashtags.randomsky.addon.bot;

import me.randomhashtags.randomsky.addon.obj.AutoBotUpgradeInfo;
import me.randomhashtags.randomsky.addon.util.Itemable;
import me.randomhashtags.randomsky.addon.util.Nameable;

public interface AutoBotUpgrade extends Itemable, Nameable {
    String getType();
    AutoBotUpgradeInfo getUpgrades();
}
