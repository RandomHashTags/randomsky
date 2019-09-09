package me.randomhashtags.randomsky.addons;

import me.randomhashtags.randomsky.addons.util.Rewardable;

public interface Lootbox extends Rewardable {
    long getExpiration();
}
