package me.randomhashtags.randomsky.addons;

import me.randomhashtags.randomsky.addons.utils.Itemable;
import org.bukkit.enchantments.Enchantment;

import java.util.List;

public abstract class VanillaEnchantRarity extends Itemable {
    public abstract List<String> getAppliesTo();
    public abstract List<Enchantment> getRevealedEnchants();
}
