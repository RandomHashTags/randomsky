package me.randomhashtags.randomsky.addon;

import me.randomhashtags.randomsky.addon.util.Rewardable;

public interface Lootbox extends Rewardable {
    long getExpiration();
}
