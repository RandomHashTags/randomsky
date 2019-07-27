package me.randomhashtags.randomsky.addons;

import me.randomhashtags.randomsky.addons.utils.Rewardable;

import java.util.List;

public abstract class Adventure extends Rewardable {
    public abstract int getSlot();
    public abstract List<String> getItemLimitations();
    public abstract AdventureMap getRequiredMap();
}
