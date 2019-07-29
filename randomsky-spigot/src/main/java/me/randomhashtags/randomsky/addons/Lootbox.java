package me.randomhashtags.randomsky.addons;

import me.randomhashtags.randomsky.addons.utils.Rewardable;

public interface Lootbox extends Rewardable {
    long getExpiration();
}
