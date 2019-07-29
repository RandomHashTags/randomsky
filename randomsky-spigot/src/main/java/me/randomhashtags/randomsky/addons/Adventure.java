package me.randomhashtags.randomsky.addons;

import me.randomhashtags.randomsky.addons.utils.Rewardable;

import java.util.List;

public interface Adventure extends Rewardable {
    int getSlot();
    List<String> getItemLimitations();
    AdventureMap getRequiredMap();
}
