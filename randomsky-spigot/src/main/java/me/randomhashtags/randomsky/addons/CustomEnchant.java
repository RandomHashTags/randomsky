package me.randomhashtags.randomsky.addons;

import me.randomhashtags.randomsky.addons.utils.Identifyable;

import java.util.List;

public interface CustomEnchant extends Identifyable {
    boolean isEnabled();
    String getName();
    List<String> getLore();
    List<String> getAppliesTo();
}
