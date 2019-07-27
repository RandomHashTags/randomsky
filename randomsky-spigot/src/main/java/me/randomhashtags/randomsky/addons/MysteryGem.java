package me.randomhashtags.randomsky.addons;

import me.randomhashtags.randomsky.addons.utils.Itemable;

import java.util.List;

public abstract class MysteryGem extends Itemable {
    public abstract List<CustomEnchantRarity> getRevealedRarities();
}
