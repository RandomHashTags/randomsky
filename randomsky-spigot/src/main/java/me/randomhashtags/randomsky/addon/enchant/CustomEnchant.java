package me.randomhashtags.randomsky.addon.enchant;

import me.randomhashtags.randomsky.addon.util.Identifiable;

import java.util.List;

public interface CustomEnchant extends Identifiable {
    boolean isEnabled();
    String getName();
    List<String> getLore();
    List<String> getAppliesTo();
}
