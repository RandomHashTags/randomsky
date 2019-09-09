package me.randomhashtags.randomsky.addons;

import me.randomhashtags.randomsky.addons.util.Itemable;

import java.util.List;

public interface MysteryGem extends Itemable {
    List<CustomEnchantRarity> getRevealedRarities();
}
