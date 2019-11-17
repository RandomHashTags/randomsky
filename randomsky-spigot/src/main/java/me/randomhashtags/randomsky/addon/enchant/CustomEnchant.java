package me.randomhashtags.randomsky.addon.enchant;

import me.randomhashtags.randomsky.addon.util.Identifiable;
import me.randomhashtags.randomsky.addon.util.Nameable;

import java.util.List;

public interface CustomEnchant extends Identifiable, Nameable {
    boolean isEnabled();
    String getName();
    List<String> getLore();
    List<String> getAppliesTo();
}
