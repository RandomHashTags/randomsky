package me.randomhashtags.randomsky.addon;

import me.randomhashtags.randomsky.addon.enchant.CustomEnchantRarity;
import me.randomhashtags.randomsky.addon.util.Itemable;

import java.util.List;

public interface MysteryGem extends Itemable {
    List<CustomEnchantRarity> getRevealedRarities();
}
