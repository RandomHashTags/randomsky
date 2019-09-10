package me.randomhashtags.randomsky.addon.enchant;

import me.randomhashtags.randomsky.addon.util.Itemable;
import org.bukkit.enchantments.Enchantment;

import java.util.List;

public interface VanillaEnchantRarity extends Itemable {
    List<String> getAppliesTo();
    List<Enchantment> getRevealedEnchants();
}
