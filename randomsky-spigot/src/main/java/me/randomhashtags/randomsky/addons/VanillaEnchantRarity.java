package me.randomhashtags.randomsky.addons;

import me.randomhashtags.randomsky.addons.util.Itemable;
import org.bukkit.enchantments.Enchantment;

import java.util.List;

public interface VanillaEnchantRarity extends Itemable {
    List<String> getAppliesTo();
    List<Enchantment> getRevealedEnchants();
}
