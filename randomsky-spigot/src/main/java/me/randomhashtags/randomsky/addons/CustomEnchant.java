package me.randomhashtags.randomsky.addons;

import me.randomhashtags.randomsky.addons.utils.Identifyable;

import java.util.List;

public abstract class CustomEnchant extends Identifyable {
    public abstract boolean isEnabled();
    public abstract String getName();
    public abstract List<String> getLore();
    public abstract List<String> getAppliesTo();
}
